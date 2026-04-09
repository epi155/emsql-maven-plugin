package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.OutputModel;

public class TdOutput extends TdAbstract<OutputModel> {

    public TdOutput(CodeFactory factory) {
        super(OutputModel.class, factory);
    }

    @Override
    protected OutputModel createModelInstance() {
        return factory.newOutputModel();
    }
}
