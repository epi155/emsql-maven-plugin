package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InlineProcedureModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInlineProcedure extends TypeDescription {
    private final CodeFactory factory;

    public TdInlineProcedure(CodeFactory factory) {
        super(InlineProcedureModel.class, "!InlineProcedure");
        this.factory = factory;
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newInlineProcedureModel();
    }
}
