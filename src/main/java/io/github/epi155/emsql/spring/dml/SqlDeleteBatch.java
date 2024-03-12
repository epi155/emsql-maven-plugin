package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dml.ApiDelete;
import io.github.epi155.emsql.commons.dml.DelegateDelete;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.*;

public class SqlDeleteBatch extends SpringAction implements ApiDelete, DeleteBatchModel {
    private final DelegateDelete delegateDelete;
    @Getter
    @Setter
    private InputModel input;
    @Setter
    private int batchSize = 1024;

    public SqlDeleteBatch() {
        super();
        this.delegateDelete = new DelegateDelete(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateDelete.proceed(fields, false);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, @NotNull JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, jdbc, cName);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("final PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        ipw.printf("return new %s", cName);
        batchGeneric(ipw);
        ipw.putf("(ps);%n");
        ipw.ends();
        declareNextClass(ipw, cName, "SqlDeleteBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyDelete(%n");
        declareInputBatch(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInputAbs(ipw, jdbc);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.ends();
    }
}
