package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectListDynModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

import java.util.Map;

public class TdSelectListDyn extends TdAbstract<SelectListDynModel> {

    public TdSelectListDyn(CodeFactory factory) {
        super(SelectListDynModel.class, "!SelectListDyn", factory);
    }

    @Override
    protected SelectListDynModel createModelInstance() {
        return factory.newSelectListDynModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
        substituteProperty("optional-and", Map.class, null, "setOptionalAnd");
    }
}
