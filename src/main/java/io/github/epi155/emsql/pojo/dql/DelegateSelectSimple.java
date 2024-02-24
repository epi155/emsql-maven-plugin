package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.dql.ApiSelectSimple;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DelegateSelectSimple {
    private final ApiSelectSimple api;

    public DelegateSelectSimple(ApiSelectSimple api) {
        this.api = api;
    }

    public void fetch(@NotNull PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        ipw.printf("        final Connection c");
        api.declareInput(ipw, jdbc);
        api.declareOutput(ipw);
        ipw.more();
        Map<Integer, SqlParam> notScalar = api.notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            api.expandIn(ipw, notScalar, kPrg);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }
        ipw.more();
        api.setInput(ipw, jdbc);
        ipw.printf("ps.setFetchSize(2);%n");
        ipw.printf("ps.setMaxRows(2);%n");
        api.setQueryHints(ipw);
        api.debugAction(ipw, kPrg, jdbc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        api.fetch(ipw, jdbc.getOMap());
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("throw SqlCode.N811.getInstance();%n");
    }
}
