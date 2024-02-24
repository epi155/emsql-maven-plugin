package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectOptionalModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdSelectOptional extends TypeDescription {
    private final CodeFactory factory;

    public TdSelectOptional(CodeFactory factory) {
        super(SelectOptionalModel.class, "!SelectOptional");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
    public Object newInstance(Node node) {
        return factory.newSelectOptionalModel();
    }

}
