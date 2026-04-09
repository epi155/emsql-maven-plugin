package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InputModel;

public class TdInput extends TdAbstract<InputModel> {

    public TdInput(CodeFactory factory) {
        super(InputModel.class, factory);
    }

    @Override
    protected InputModel createModelInstance() {
        return factory.newInputModel();
    }
}
