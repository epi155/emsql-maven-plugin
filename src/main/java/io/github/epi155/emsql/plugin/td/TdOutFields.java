package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.FieldsModel;

public class TdOutFields extends TdAbstract<FieldsModel> {

    public TdOutFields(CodeFactory factory) {
        super(FieldsModel.class, factory);
    }

    @Override
    protected FieldsModel createModelInstance() {
        return factory.newOutFieldsModel();
    }
}
