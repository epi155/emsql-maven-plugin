package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dpl.ApiWrite;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class DelegateWrite {
    private final ApiWrite api;

    public DelegateWrite(ApiWrite api) {
        this.api = api;
    }

    public void proceed(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docOutput(ipw, jdbc.getOMap());
        api.docEnd(ipw);

        cc.add("org.springframework.transaction.annotation.Transactional");
        ipw.printf("@Transactional%n");
        ipw.printf("public ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());

        if (mc.oSize() == 0) {
            ipw.putf("void %s(", name);
        } else if (mc.oSize() == 1) {
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(", v.getType().getPrimitive(), name));
        } else {
            ipw.putf("O %s(", name);
        }
        ipw.commaReset();

        api.declareInput(ipw, jdbc);
        api.declareOutput(ipw);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        api.debugAction(ipw, kPrg, jdbc);
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        api.setInputAbs(ipw, jdbc);
        api.registerOutAbs(ipw, jdbc.getOMap());
        api.setQueryHints(ipw);
        ipw.printf("ps.execute();%n");
        api.getOutput(ipw, jdbc.getOMap());
        if (mc.oSize()>0)
            ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }
}
