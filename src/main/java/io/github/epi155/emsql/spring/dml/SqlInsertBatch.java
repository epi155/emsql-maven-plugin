package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dml.ApiInsert;
import io.github.epi155.emsql.commons.dml.DelegateInsert;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.*;

public class SqlInsertBatch extends SpringAction implements ApiInsert, InsertBatchModel {
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private InputModel input;
    @Setter
    private int batchSize = 1024;

    public SqlInsertBatch() {
        super();
        this.delegateInsert = new DelegateInsert(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInsert.proceed(fields);
    }
    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (1<nSize && nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlInsertBatch", jdbc, cName);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("final PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareInnerClass(ipw, cName, "SqlInsertBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInsert(%n");
        declareInputBatch(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, jdbc);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.ends();
        ipw.printf("return new %s();%n", cName);
        ipw.ends();
    }
}
