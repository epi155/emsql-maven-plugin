package io.github.epi155.emsql.spring;

import lombok.Setter;

import static io.github.epi155.emsql.commons.Contexts.IMAX;

@Setter
public abstract class SpringBatchAction extends SpringAction {
    protected int batchSize = 1024;
    private boolean force;

    @Override
    public boolean isUnboxRequest(int size) {
        if (size <= 1) return true;
        if (force) return false;
        return size <= IMAX;
    }
}
