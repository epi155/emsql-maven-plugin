package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InsertBatchModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdInsertBatch extends TdAbstract<InsertBatchModel> {

    public TdInsertBatch(CodeFactory factory) {
        super(InsertBatchModel.class, "!InsertBatch", factory);
    }

    @Override
    protected InsertBatchModel createModelInstance() {
        return factory.newInsertBatchModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
