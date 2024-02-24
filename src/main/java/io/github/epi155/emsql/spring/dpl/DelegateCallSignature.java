package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiSelectSignature;

import static io.github.epi155.emsql.commons.Contexts.cc;

public class DelegateCallSignature {
    private final ApiSelectSignature api;

    public DelegateCallSignature(ApiSelectSignature api) {
        this.api = api;
    }

    public void signature(PrintModel ipw, JdbcStatement jdbc, String name) {
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        cc.add("org.springframework.transaction.annotation.Transactional");
        ipw.printf("@Transactional%n");
        ipw.printf("public ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
    }
}
