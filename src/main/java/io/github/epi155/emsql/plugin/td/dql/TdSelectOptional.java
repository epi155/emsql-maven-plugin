package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectOptionalModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdSelectOptional extends TdAbstract<SelectOptionalModel> {
    
    public TdSelectOptional(CodeFactory factory) {
        super(SelectOptionalModel.class, "!SelectOptional", factory);
    }

    @Override
    protected SelectOptionalModel createModelInstance() {
        return factory.newSelectOptionalModel();
    }
}