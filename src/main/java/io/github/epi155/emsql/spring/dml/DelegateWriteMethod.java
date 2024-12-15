package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;

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

        cc.add("org.springframework.transaction.annotation.Transactional");
        ipw.printf("@Transactional%n");
        ipw.printf("public ");
        api.declareGenerics(ipw, cName, jdbc.getTKeys());
        ipw.putf("int %s(", name);
        ipw.commaReset();

        api.declareInput(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        api.debugAction(ipw, kPrg, jdbc);
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
        ipw.printf("return ps.executeUpdate();%n");
        ipw.ends();
        ipw.ends();
    }
}
