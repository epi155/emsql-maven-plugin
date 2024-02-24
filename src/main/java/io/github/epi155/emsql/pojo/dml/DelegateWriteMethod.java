package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlParam;
import io.github.epi155.emsql.pojo.Tools;

import java.util.Map;

public class DelegateWriteMethod {
    private final ApiWriteMethod api;

    public DelegateWriteMethod(ApiWriteMethod api) {
        this.api = api;
    }

    public void proceed(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
        ipw.putf("int %s(%n", name);

        ipw.printf("        final Connection c");
        api.declareInput(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
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
        api.setQueryHints(ipw);
        api.debugAction(ipw, kPrg, jdbc);
        ipw.printf("return ps.executeUpdate();%n");
        ipw.ends();
        ipw.ends();
    }
}
