package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CallBatchModel;
import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdCallBatch extends TdAbstract<CallBatchModel> {

    public TdCallBatch(CodeFactory factory) {
        super(CallBatchModel.class, "!CallBatch", factory);
    }

    @Override
    protected CallBatchModel createModelInstance() {
        return factory.newCallBatchModel();
    }
}
