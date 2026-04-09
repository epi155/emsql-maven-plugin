package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.OutFieldsModel;

public class TdOutFields extends TdAbstract<OutFieldsModel> {

    public TdOutFields(CodeFactory factory) {
        super(OutFieldsModel.class, factory);
    }

    @Override
    protected OutFieldsModel createModelInstance() {
        return factory.newOutFieldsModel();
    }
}
