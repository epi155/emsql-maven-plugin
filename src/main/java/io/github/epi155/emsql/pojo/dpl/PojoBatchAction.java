package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.*;

@Setter
public abstract class PojoBatchAction extends PojoAction {
    protected int batchSize = 1024;
    private boolean force;

    @Override
    public void declareNextClass(
            PrintModel ipw,
            String cName,
            String eSqlObject,
            JdbcStatement jdbc,
            int batchSize,
            String kPrg) throws InvalidQueryException {
        ipw.printf("public static class %s", cName);
        String iName = cc.inPrepare(cName, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(cName, jdbc.getOMap().values(), mc);
        declareGenerics(ipw, jdbc.getTKeys(), iName, oName);
        ipw.putf(" extends %s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf("{%n");
        ipw.more();
        ipw.printf("protected %s(CallableStatement ps) {%n", cName);
        ipw.more();
        ipw.printf("super(Q_%s, ps, %d);%n", kPrg, batchSize);
        ipw.ends();
    }

    @Override
    public boolean isUnboxRequest(int size) {
        if (size <= 1) return true;
        if (force) return false;
        return size <= IMAX;
    }
}
