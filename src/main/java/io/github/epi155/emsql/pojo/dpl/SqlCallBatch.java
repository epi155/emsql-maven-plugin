package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dpl.ApiCall;
import io.github.epi155.emsql.commons.dpl.DelegateCall;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCallBatch extends PojoBatchAction
        implements ApiCall, CallBatchModel {
    private final DelegateCall delegateCall;

    @Setter
    @Getter
    private InputModel input;
    @Setter
    @Getter
    private OutFieldsModel output;
    @Setter
    @Getter
    private InOutFieldsModel inputOutput;

    public SqlCallBatch() {
        super();
        this.delegateCall = new DelegateCall(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateCall.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (isUnboxRequest(nSize)) {
            cc.add("io.github.epi155.emsql.runtime.SqlCallBatch" + nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlCallBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, cName);
        ipw.more();
        debugQuery(ipw, kPrg);
        ipw.printf("final CallableStatement ps = c.prepareCall(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        ipw.printf("return new %s", cName);
        batchGeneric(ipw);
        ipw.putf("(ps);%n");
        ipw.ends();
        declareNextClass(ipw, cName, "SqlCallBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyCall(%n");
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
