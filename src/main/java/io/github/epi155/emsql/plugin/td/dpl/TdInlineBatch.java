package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InlineBatchModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdInlineBatch extends TdAbstract<InlineBatchModel> {

    public TdInlineBatch(CodeFactory factory) {
        super(InlineBatchModel.class, "!InlineBatch", factory);
    }

    @Override
    protected InlineBatchModel createModelInstance() {
        return factory.newInlineBatchModel();
    }
}
