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

public class SqlSelectOptional extends SqlAction
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

    SqlSelectOptional() {
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
        cc.add("java.util.Optional");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (jdbc.getOutSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("Optional<%s> %s(%n", v.getType().getWrapper(), name));
        } else {
            if (output != null && output.isDelegate()) {
                ipw.putf("boolean %s(%n", name);
            } else {
                ipw.putf("Optional<O> %s(%n", name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, name, kPrg, cc);
        ipw.orElse();
        if (output != null && output.isDelegate() && jdbc.getOutSize() > 1) {
            ipw.printf("return true;%n");
        } else {
            ipw.printf("return Optional.of(o);%n");
        }
        ipw.ends();
        ipw.orElse();
        if (output != null && output.isDelegate() && jdbc.getOutSize() > 1) {
            ipw.printf("return false;%n");
        } else {
            ipw.printf("return Optional.empty();%n");
        }
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();

    }
}
