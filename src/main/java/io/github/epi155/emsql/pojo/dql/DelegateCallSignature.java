package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.Tools;

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

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
    }
}
