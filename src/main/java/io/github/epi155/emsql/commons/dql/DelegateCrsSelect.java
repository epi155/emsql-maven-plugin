package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;

import java.util.Map;

public class DelegateCrsSelect {
    private final ApiCrsSelect api;

    public DelegateCrsSelect(ApiCrsSelect api) {
        this.api = api;
    }

    public void writeOpenCode(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();

        Map<Integer, SqlParam> notScalar = api.notScalar(jdbc.getIMap());
        if (!notScalar.isEmpty()) {
            api.expandIn(ipw, notScalar, kPrg);
        }
        if (oSize == 1) {
            ipw.printf("return new SqlCursor<%s>() {%n", oMap.get(1).getType().getWrapper());
        } else {
            ipw.printf("return new SqlCursor<O>() {%n");
        }
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");
        ipw.more();
        api.debugAction(ipw, kPrg, jdbc);
        ipw.printf("try {%n");
        ipw.more();
        if (notScalar.isEmpty()) {
            ipw.printf("this.ps = c.prepareStatement(Q_%s);%n", kPrg);
        } else {
            ipw.printf("this.ps = c.prepareStatement(query);%n");
        }
        api.setInput(ipw, jdbc);
        if (api.getFetchSize() != null) ipw.printf("ps.setFetchSize(%d);%n", api.getFetchSize());
        api.setQueryHints(ipw);
        ipw.printf("this.rs = ps.executeQuery();%n");
        api.dumpAction(ipw, kPrg, jdbc);
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public boolean hasNext() throws SQLException {%n");
        ipw.more();
        ipw.printf("return rs.next();%n");
        ipw.ends();
        ipw.printf("@Override%n");
        if (oSize == 1) {
            ipw.printf("public %s fetchNext() throws SQLException {%n", oMap.get(1).getType().getWrapper());
        } else {
            ipw.printf("public O fetchNext() throws SQLException {%n");
        }
        ipw.more();
        api.fetch(ipw, oMap);
        ipw.printf("return o;%n");
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public void close() throws SQLException {%n");
        ipw.more();
        ipw.printf("if (rs != null) rs.close();%n");
        ipw.printf("ps.close();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n"); // close anonymous class statement
        ipw.ends();
    }

    public void writeForEachCode(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();

        api.debugAction(ipw, kPrg, jdbc);
        api.openQuery(ipw, jdbc, kPrg);
        ipw.more();
        api.setInput(ipw, jdbc);
        if (api.getFetchSize() != null) ipw.printf("ps.setFetchSize(%d);%n", api.getFetchSize());
        api.setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        api.fetch(ipw, oMap);
        ipw.printf("co.accept(o);%n");
        ipw.ends(); // end while
        ipw.ends(); // end executeQuery
        api.dumpAction(ipw, kPrg, jdbc);
        ipw.ends(); // end method
    }
}
