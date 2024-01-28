package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAreaStd;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlSelectSingle extends SqlAction
        implements ApiSelectFields, ApiSelectSignature, ApiSelectSimple {
    private final DelegateSelectFields delegateSelectFields;
    private final DelegateSelectSignature delegateSelectSignature;
    private final DelegateSelectSimple delegateSelectSimple;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    private ComAreaStd output;

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
        cc.add("io.github.epi155.esql.runtime.ESqlCode");
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
        ipw.printf("throw ESqlCode.P100.getInstance();%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
