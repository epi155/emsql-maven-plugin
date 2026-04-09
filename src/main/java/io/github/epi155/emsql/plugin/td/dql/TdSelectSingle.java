package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectSingleModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdSelectSingle extends TdAbstract<SelectSingleModel> {

    public TdSelectSingle(CodeFactory factory) {
        super(SelectSingleModel.class, "!SelectSingle", factory);
    }

    @Override
    protected SelectSingleModel createModelInstance() {
        return factory.newSelectSingleModel();
    }
}
