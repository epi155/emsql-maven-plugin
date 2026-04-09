package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InsertReturnGeneratedKeysModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdInsertReturnGeneratedKeys extends TdAbstract<InsertReturnGeneratedKeysModel> {

    public TdInsertReturnGeneratedKeys(CodeFactory factory) {
        super(InsertReturnGeneratedKeysModel.class, "!InsertReturnKeys", factory);
    }

    @Override
    protected InsertReturnGeneratedKeysModel createModelInstance() {
        return factory.newInsertReturnGeneratedKeysModel();
    }
}
