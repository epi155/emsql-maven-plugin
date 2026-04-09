package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.DeleteModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdDelete extends TdAbstract<DeleteModel> {

    public TdDelete(CodeFactory factory) {
        super(DeleteModel.class, "!Delete", factory);
    }

    @Override
    protected DeleteModel createModelInstance() {
        return factory.newDeleteModel();
    }
}
