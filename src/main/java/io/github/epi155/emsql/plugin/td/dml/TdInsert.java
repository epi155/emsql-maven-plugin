package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InsertModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInsert extends TypeDescription {
    private final CodeFactory factory;

    public TdInsert(CodeFactory factory) {
        super(InsertModel.class, "!Insert");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
    public Object newInstance(Node node) {
        return factory.newInsertModel();
    }
}
