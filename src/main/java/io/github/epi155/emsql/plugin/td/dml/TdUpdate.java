package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.UpdateModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdUpdate extends TypeDescription {
    private final CodeFactory factory;

    public TdUpdate(CodeFactory factory) {
        super(UpdateModel.class, "!Update");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newUpdateModel();
    }
}
