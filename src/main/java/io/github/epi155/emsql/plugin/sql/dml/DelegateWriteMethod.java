package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlParam;

import java.util.Map;

public class DelegateWriteMethod {
    private final ApiWriteMethod api;

    public DelegateWriteMethod(ApiWriteMethod api) {
        this.api = api;
    }

    public void proceed(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        int iSize = iMap.size();
        String cName = Tools.capitalize(name);
        api.docBegin(ipw);
        api.docInput(ipw, jdbc);
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, iSize, 1, jdbc.getTKeys());
        ipw.putf("int %s(%n", name);

        ipw.printf("        final Connection c");
        api.declareInput(ipw, jdbc, cc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        Map<Integer, SqlParam> notScalar = api.notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            api.expandIn(ipw, notScalar, kPrg, jdbc.getNameSize(), cc);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }
        ipw.more();
        api.setInput(ipw, jdbc, cc);
        api.setQueryHints(ipw);
        api.debugAction(ipw, kPrg, jdbc, cc);
        ipw.printf("return ps.executeUpdate();%n");
        ipw.ends();
        ipw.ends();
    }
}
