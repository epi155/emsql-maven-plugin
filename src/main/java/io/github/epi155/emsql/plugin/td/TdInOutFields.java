package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InOutFieldsModel;

public class TdInOutFields extends TdAbstract<InOutFieldsModel> {

    public TdInOutFields(CodeFactory factory) {
        super(InOutFieldsModel.class, factory);
    }

    @Override
    protected InOutFieldsModel createModelInstance() {
        return factory.newInOutFieldsModel();
    }
}
