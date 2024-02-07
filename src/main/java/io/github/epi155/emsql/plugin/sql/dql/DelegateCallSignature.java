package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;

public class DelegateCallSignature {
    private final ApiSelectSignature api;

    public DelegateCallSignature(ApiSelectSignature api) {
        this.api = api;
    }

    public void signature(IndentPrintWriter ipw, JdbcStatement jdbc, String name) {
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, jdbc.getNameSize(), jdbc.getOutSize());
    }
}
