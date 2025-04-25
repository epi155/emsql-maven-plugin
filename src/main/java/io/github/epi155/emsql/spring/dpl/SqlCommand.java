package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.spring.SpringAction;

import java.util.Collections;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;

public class SqlCommand extends SpringAction implements CommandModel {
    public SqlCommand() {
        super();
    }

    @Override
    public InputModel getInput() {
        return null;
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(getExecSql());
        return Tools.replacePlaceholder(nText, Collections.emptyMap(), Collections.emptyMap());
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        docBegin(ipw);
        docEnd(ipw);

        cc.add("org.springframework.transaction.annotation.Transactional");
        ipw.printf("@Transactional%n");
        ipw.printf("public ");
        ipw.putf("void %s(", name);
        ipw.commaReset();
        declareOutput(ipw);

        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("try (Statement st = c.createStatement()) {%n");
        ipw.more();
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("st.execute(Q_%s);%n", kPrg);
        ipw.ends();
        ipw.ends();
    }

    @Override
    public void declareNextClass(
            PrintModel ipw,
            String name,
            String eSqlObject,
            JdbcStatement jdbc,
            int batchSize,
            String kPrg) {
        throw new IllegalStateException();  // only batch mode
    }
}
