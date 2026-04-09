package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InlineProcedureModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdInlineProcedure extends TdAbstract<InlineProcedureModel> {

    public TdInlineProcedure(CodeFactory factory) {
        super(InlineProcedureModel.class, "!InlineProcedure", factory);
    }

    @Override
    protected InlineProcedureModel createModelInstance() {
        return factory.newInlineProcedureModel();
    }
}
