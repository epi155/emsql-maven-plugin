package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.Tools;

import static io.github.epi155.emsql.pojo.Tools.mc;

public class DelegateSelectSignature {
    private final ApiSelectSignature api;

    public DelegateSelectSignature(ApiSelectSignature api) {
        this.api = api;
    }

    public void signature(PrintModel ipw, JdbcStatement jdbc, String name) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
    }
}
