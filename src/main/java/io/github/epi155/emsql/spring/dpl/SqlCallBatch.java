package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.CallBatchModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.*;

@Setter
public class SqlCallBatch extends SqlCallProcedure implements CallBatchModel {
    private int batchSize = 1024;

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (1<nSize && nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlCallBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlCallBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlCallBatch", jdbc, cName);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("final CallableStatement ps = c.prepareCall(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareInnerClass(ipw, cName, "SqlCallBatch", jdbc, batchSize, kPrg);
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
        ipw.printf("return new %s();%n", cName);
        ipw.ends();
    }
}
