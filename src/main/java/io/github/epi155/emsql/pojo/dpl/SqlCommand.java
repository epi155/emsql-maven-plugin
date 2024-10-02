package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.pojo.PojoAction;

import java.util.Collections;
import java.util.Map;

public class SqlCommand extends PojoAction implements CommandModel {
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

        ipw.printf("public static ");
        ipw.putf("void %s(%n", name);
        ipw.printf("        final Connection c");
        declareOutput(ipw);

        ipw.more();
        ipw.printf("try (Statement st = c.createStatement()) {%n");
        ipw.more();
        setQueryHints(ipw);
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
