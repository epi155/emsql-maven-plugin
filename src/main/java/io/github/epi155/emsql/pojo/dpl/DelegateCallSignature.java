package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;

public class DelegateCallSignature {
    private final ApiDocSignature api;

    public DelegateCallSignature(ApiDocSignature api) {
        this.api = api;
    }

    public void signature(PrintModel ipw, JdbcStatement jdbc, String name) {
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
    }
}
