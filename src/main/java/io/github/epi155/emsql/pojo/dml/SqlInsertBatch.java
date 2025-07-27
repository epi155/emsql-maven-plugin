package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dml.ApiInsert;
import io.github.epi155.emsql.commons.dml.DelegateInsert;
import io.github.epi155.emsql.pojo.PojoBatchAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlInsertBatch extends PojoBatchAction implements ApiInsert, InsertBatchModel {
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private InputModel input;

    public SqlInsertBatch() {
        super();
        this.delegateInsert = new DelegateInsert(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInsert.proceed(fields, false);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (isUnboxRequest(nSize)) {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch" + nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, cName);
        ipw.more();
        debugQuery(ipw, kPrg);
        ipw.printf("final PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        ipw.printf("return new %s", cName);
        batchGeneric(ipw);
        ipw.putf("(ps);%n");
        ipw.ends();
        declareNextClass(ipw, cName, "SqlInsertBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInsert(%n");
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
