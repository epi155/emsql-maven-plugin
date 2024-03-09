package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.InlineBatchModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.*;

@Setter
public class SqlInlineBatch extends SqlInlineProcedure implements InlineBatchModel {
    private int batchSize = 1024;

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlInlineBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlInlineBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlInlineBatch", jdbc, cName);
        ipw.more();
        ipw.printf("final CallableStatement ps = c.prepareCall(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareInnerClass(ipw, cName, "SqlInlineBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInline(%n");
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
