package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.OutFieldsModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdOutFields extends TypeDescription {
    private final CodeFactory factory;

    public TdOutFields(CodeFactory factory) {
        super(OutFieldsModel.class);
        this.factory = factory;
    }
    public Object newInstance(Node node) {
        return factory.newOutFieldsModel();
    }
}
