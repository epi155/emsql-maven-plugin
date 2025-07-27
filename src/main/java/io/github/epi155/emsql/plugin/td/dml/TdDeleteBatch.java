package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.DeleteBatchModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdDeleteBatch extends TypeDescription {
    private final CodeFactory factory;

    public TdDeleteBatch(CodeFactory factory) {
        super(DeleteBatchModel.class, "!DeleteBatch");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }

    public Object newInstance(Node node) {
        return factory.newDeleteBatchModel();
    }
}
