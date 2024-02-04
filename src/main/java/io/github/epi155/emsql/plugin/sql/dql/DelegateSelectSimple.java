package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;

public class DelegateSelectSimple {
    private final ApiSelectSimple api;

    public DelegateSelectSimple(ApiSelectSimple api) {
        this.api = api;
    }

    public void fetch(IndentPrintWriter ipw, JdbcStatement jdbc, String name, String kPrg, ClassContext cc) {
        ipw.printf("        final Connection c");
        api.declareInput(ipw, jdbc.getIMap(), Tools.capitalize(name));
        api.declareOutput(ipw, jdbc.getOutSize(), cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        api.setInput(ipw, jdbc.getIMap());
        ipw.printf("ps.setFetchSize(2);%n");
        if (api.getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", api.getTimeout());
        api.debugAction(ipw, kPrg, jdbc.getIMap(), cc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        api.fetch(ipw, jdbc.getOMap(), cc);
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("throw SqlCode.N811.getInstance();%n");
    }
}
