package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CallProcedureModel;
import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.FieldsModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdCallProcedure extends TdAbstract<CallProcedureModel> {

    public TdCallProcedure(CodeFactory factory) {
        super(CallProcedureModel.class, "!CallProcedure", factory);
    }

    @Override
    protected CallProcedureModel createModelInstance() {
        return factory.newCallProcedureModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("input-output", FieldsModel.class, null, "setInputOutput");
    }
}
