package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.MethodModel;

public class TdMethod extends TdAbstract<MethodModel> {

    public TdMethod(CodeFactory factory) {
        super(MethodModel.class, factory);
    }

    @Override
    protected MethodModel createModelInstance() {
        return factory.newMethodModel();
    }
}
