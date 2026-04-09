package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CursorForSelectModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdCursorForSelect extends TdAbstract<CursorForSelectModel> {

    public TdCursorForSelect(CodeFactory factory) {
        super(CursorForSelectModel.class, "!CursorForSelect", factory);
    }

    @Override
    protected CursorForSelectModel createModelInstance() {
        return factory.newCursorForSelectModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }
}
