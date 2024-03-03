package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class DelegateSelectSignature {
    private final ApiDocSignature api;

    public DelegateSelectSignature(ApiDocSignature api) {
        this.api = api;
    }

    public void signature(PrintModel ipw, JdbcStatement jdbc, String name) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        cc.add("org.springframework.transaction.annotation.Transactional");
        ipw.printf("@Transactional(readOnly=true)%n");
        ipw.printf("public ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
    }
}
