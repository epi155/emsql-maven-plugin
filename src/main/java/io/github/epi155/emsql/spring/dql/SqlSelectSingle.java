package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.ApiSelectSimple;
import io.github.epi155.emsql.commons.dql.DelegateSelectFields;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectSingle extends SpringAction
        implements ApiSelectFields, ApiDocSignature, ApiSelectSimple, SelectSingleModel {
    protected final DelegateSelectFields delegateSelectFields;
    protected final DelegateSelectSignature delegateSelectSignature;
    protected final DelegateSelectSimple delegateSelectSimple;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    protected OutputModel output;

    public SqlSelectSingle() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
        this.delegateSelectSignature = new DelegateSelectSignature(this);
        this.delegateSelectSimple = new DelegateSelectSimple(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("io.github.epi155.emsql.runtime.SqlCode");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            if (mc.isOutputDelegate()) {
                ipw.putf("void %s(%n", name);
            } else {
                ipw.putf("O %s(%n", name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, kPrg);
        if (mc.oSize()==1 || !mc.isOutputDelegate()) {
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
