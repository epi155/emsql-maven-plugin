package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InOutFieldsModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInOutFields extends TypeDescription {
    private final CodeFactory factory;

    public TdInOutFields(CodeFactory factory) {
        super(InOutFieldsModel.class);
        this.factory = factory;
    }

    public Object newInstance(Node node) {
        return factory.newInOutFieldsModel();
    }
}
