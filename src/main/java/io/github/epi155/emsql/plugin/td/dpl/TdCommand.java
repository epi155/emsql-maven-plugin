package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CommandModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdCommand extends TypeDescription {
    private final CodeFactory factory;

    public TdCommand(CodeFactory factory) {
        super(CommandModel.class, "!Command");
        this.factory = factory;
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    @Override
    public Object newInstance(Node node) {
        return factory.newCommandModel();
    }
}
