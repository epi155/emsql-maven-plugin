package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectListModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdSelectList extends TdAbstract<SelectListModel> {

    public TdSelectList(CodeFactory factory) {
        super(SelectListModel.class, "!SelectList", factory);
    }

    @Override
    protected SelectListModel createModelInstance() {
        return factory.newSelectListModel();
    }

    @Override
    protected void substituteAdditionalProperties() {
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }
}
