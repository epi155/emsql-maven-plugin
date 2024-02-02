package io.github.epi155.esql.plugin.sql.dml;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlParam;

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
        api.docInput(ipw, iMap);
        api.docEnd(ipw);

        ipw.printf("public static ");
        api.declareGenerics(ipw, cName, iSize, 1);
        ipw.putf("int %s(%n", name);

        ipw.printf("        final Connection c");
        api.declareInput(ipw, iMap, cName);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        api.setInput(ipw, iMap);
        if (api.getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", api.getTimeout());
        api.debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("return ps.executeUpdate();%n");
        ipw.ends();
        ipw.ends();
    }
}
