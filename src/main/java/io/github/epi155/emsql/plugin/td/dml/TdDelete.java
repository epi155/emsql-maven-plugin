package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.DeleteModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdDelete extends TypeDescription {
    private final CodeFactory factory;

    public TdDelete(CodeFactory factory) {
        super(DeleteModel.class, "!Delete");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newDeleteModel();
    }
}
