package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InsertModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdInsert extends TdAbstract<InsertModel> {

    public TdInsert(CodeFactory factory) {
        super(InsertModel.class, "!Insert", factory);
    }

    @Override
    protected InsertModel createModelInstance() {
        return factory.newInsertModel();
    }
}
