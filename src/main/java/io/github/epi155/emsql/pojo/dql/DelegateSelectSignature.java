package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
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
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        String iName = cc.inPrepare(name, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(name, jdbc.getOMap().values(), mc);
        ipw.printf("public static ");
        api.declareGenerics(ipw, jdbc.getTKeys(), iName, oName);
    }
}
