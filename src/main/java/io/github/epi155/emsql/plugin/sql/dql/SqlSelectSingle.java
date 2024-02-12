package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

import static io.github.epi155.emsql.plugin.Tools.cc;
import static io.github.epi155.emsql.plugin.Tools.mc;

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
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("io.github.epi155.emsql.runtime.SqlCode");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            if (mc.isOutoutDelegate()) {
                ipw.putf("void %s(%n", name);
            } else {
                ipw.putf("O %s(%n", name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, kPrg);
        if (mc.oSize()==1 || !mc.isOutoutDelegate()) {
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
