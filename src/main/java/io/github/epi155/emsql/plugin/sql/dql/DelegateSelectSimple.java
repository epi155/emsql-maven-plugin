package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import org.jetbrains.annotations.NotNull;

public class DelegateSelectSimple {
    private final ApiSelectSimple api;

    public DelegateSelectSimple(ApiSelectSimple api) {
        this.api = api;
    }

    public void fetch(@NotNull IndentPrintWriter ipw, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        ipw.printf("        final Connection c");
        api.declareInput(ipw, jdbc);
        api.declareOutput(ipw, jdbc.getOutSize(), cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        api.setInput(ipw, jdbc);
        ipw.printf("ps.setFetchSize(2);%n");
        if (api.getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", api.getTimeout());
        api.debugAction(ipw, kPrg, jdbc, cc);
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
