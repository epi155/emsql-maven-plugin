package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CommandModel;
import io.github.epi155.emsql.plugin.td.TdAbstract;

public class TdCommand extends TdAbstract<CommandModel> {

    public TdCommand(CodeFactory factory) {
        super(CommandModel.class, "!Command", factory);
    }

    @Override
    protected CommandModel createModelInstance() {
        return factory.newCommandModel();
    }
}
