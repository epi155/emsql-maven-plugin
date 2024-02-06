package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlSelectSingle extends SqlAction
        implements ApiSelectFields, ApiSelectSignature, ApiSelectSimple {
    protected final DelegateSelectFields delegateSelectFields;
    protected final DelegateSelectSignature delegateSelectSignature;
    protected final DelegateSelectSimple delegateSelectSimple;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    protected ComAreaStd output;

    SqlSelectSingle() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
        this.delegateSelectSignature = new DelegateSelectSignature(this);
        this.delegateSelectSimple = new DelegateSelectSimple(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        cc.add("io.github.epi155.emsql.runtime.SqlCode");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (jdbc.getOutSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            if (output!=null && output.isDelegate()) {
                ipw.putf("void %s(%n", name);
            } else {
                ipw.putf("O %s(%n", name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, name, kPrg, cc);
        if (jdbc.getOutSize()==1 || output==null || !output.isDelegate()) {
            ipw.orElse();
            ipw.printf("return o;%n");
        }
        ipw.ends();
        ipw.orElse();
        ipw.printf("throw SqlCode.P100.getInstance();%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
