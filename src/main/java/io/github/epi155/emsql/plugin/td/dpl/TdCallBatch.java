package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CallBatchModel;
import io.github.epi155.emsql.api.CodeFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdCallBatch extends TypeDescription {
    private final CodeFactory factory;

    public TdCallBatch(CodeFactory factory) {
        super(CallBatchModel.class, "!CallBatch");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newCallBatchModel();
    }
}
