package io.github.epi155.emsql.pojo;

import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.IMAX;

@Setter
public abstract class PojoBatchAction extends PojoAction {
    protected int batchSize = 1024;
    private boolean force;

    @Override
    public boolean isUnboxRequest(int size) {
        if (size <= 1) return true;
        if (force) return false;
        return  size<=IMAX;
    }
}
