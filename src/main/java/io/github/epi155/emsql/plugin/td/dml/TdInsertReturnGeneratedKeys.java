package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InsertReturnGeneratedKeysModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInsertReturnGeneratedKeys extends TypeDescription {
    private final CodeFactory factory;

    public TdInsertReturnGeneratedKeys(CodeFactory factory) {
        super(InsertReturnGeneratedKeysModel.class, "!InsertReturnKeys");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newInsertReturnGeneratedKeysModel();
    }
}
