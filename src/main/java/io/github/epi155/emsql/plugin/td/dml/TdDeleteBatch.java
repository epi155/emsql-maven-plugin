package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.DeleteBatchModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdDeleteBatch extends TdAbstract<DeleteBatchModel> {

    public TdDeleteBatch(CodeFactory factory) {
        super(DeleteBatchModel.class, "!DeleteBatch", factory);
    }

    @Override
    protected DeleteBatchModel createModelInstance() {
        return factory.newDeleteBatchModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
