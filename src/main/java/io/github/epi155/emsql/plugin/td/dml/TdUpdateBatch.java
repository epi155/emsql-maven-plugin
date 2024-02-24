package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.UpdateBatchModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdUpdateBatch extends TypeDescription {
    private final CodeFactory factory;

    public TdUpdateBatch(CodeFactory factory) {
        super(UpdateBatchModel.class, "!UpdateBatch");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
    public Object newInstance(Node node) {
        return factory.newUpdateBatchModel();
    }
}
