package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.DeleteBatchModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dml.ApiDelete;
import io.github.epi155.emsql.commons.dml.DelegateDelete;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlDeleteBatch extends PojoBatchAction implements ApiDelete, DeleteBatchModel {
    private final DelegateDelete delegateDelete;

    public SqlDeleteBatch() {
        super();
        this.delegateDelete = new DelegateDelete(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateDelete.proceed(fields, false);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, @NotNull JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        int nSize = mc.nSize();
        if (isUnboxRequest(nSize)) {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch" + nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        String iName = cc.inPrepare(name, jdbc.getIMap().values(), mc);
        declareNewInstance(ipw, cName, iName);
        ipw.more();
        debugQuery(ipw, kPrg);
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
