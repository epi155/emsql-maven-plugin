package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.UpdateBatchModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdUpdateBatch extends TdAbstract<UpdateBatchModel> {

    public TdUpdateBatch(CodeFactory factory) {
        super(UpdateBatchModel.class, "!UpdateBatch", factory);
    }

    @Override
    protected UpdateBatchModel createModelInstance() {
        return factory.newUpdateBatchModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
