package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.UpdateModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdUpdate extends TdAbstract<UpdateModel> {

    public TdUpdate(CodeFactory factory) {
        super(UpdateModel.class, "!Update", factory);
    }

    @Override
    protected UpdateModel createModelInstance() {
        return factory.newUpdateModel();
    }
}
