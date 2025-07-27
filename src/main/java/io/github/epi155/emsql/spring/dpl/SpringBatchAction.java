package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.IMAX;

@Setter
public abstract class SpringBatchAction extends SpringAction {
    protected int batchSize = 1024;
    private boolean force;

    @Override
    public void declareNextClass(
            PrintModel ipw,
            String name,
            String eSqlObject,
            JdbcStatement jdbc,
            int batchSize,
            String kPrg) {
        ipw.printf("public static class %s", name);
        declareGenerics(ipw, name, jdbc.getTKeys());
        ipw.putf(" extends %s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf("{%n");
        ipw.more();
        ipw.printf("protected %s(CallableStatement ps) {%n", name);
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
