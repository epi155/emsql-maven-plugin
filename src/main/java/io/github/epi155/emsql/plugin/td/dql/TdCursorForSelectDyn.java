package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CursorForSelectDynModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

import java.util.Map;

public class TdCursorForSelectDyn extends TdAbstract<CursorForSelectDynModel> {

    public TdCursorForSelectDyn(CodeFactory factory) {
        super(CursorForSelectDynModel.class, "!CursorForSelectDyn", factory);
    }

    @Override
    protected CursorForSelectDynModel createModelInstance() {
        return factory.newCursorForSelectDynModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
        substituteProperty("optional-and", Map.class, null, "setOptionalAnd");
    }
}
