package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InlineBatchModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInlineBatch extends TypeDescription {
    private final CodeFactory factory;

    public TdInlineBatch(CodeFactory factory) {
        super(InlineBatchModel.class, "!InlineBatch");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newInlineBatchModel();
    }
}
