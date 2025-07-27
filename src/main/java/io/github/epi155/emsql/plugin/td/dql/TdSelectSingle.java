package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectSingleModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdSelectSingle extends TypeDescription {
    private final CodeFactory factory;

    public TdSelectSingle(CodeFactory factory) {
        super(SelectSingleModel.class, "!SelectSingle");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }

    public Object newInstance(Node node) {
        return factory.newSelectSingleModel();
    }
}
