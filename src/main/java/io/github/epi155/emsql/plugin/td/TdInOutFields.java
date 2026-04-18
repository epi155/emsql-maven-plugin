package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.FieldsModel;

public class TdInOutFields extends TdAbstract<FieldsModel> {

    public TdInOutFields(CodeFactory factory) {
        super(FieldsModel.class, factory);
    }

    @Override
    protected FieldsModel createModelInstance() {
        return factory.newInOutFieldsModel();
    }
}
