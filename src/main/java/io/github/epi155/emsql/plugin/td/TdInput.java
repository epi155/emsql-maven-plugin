package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InputModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdInput extends TypeDescription {
    private final CodeFactory factory;

    public TdInput(CodeFactory factory) {
        super(InputModel.class);
        this.factory = factory;
    }

    public Object newInstance(Node node) {
        return factory.newInputModel();
    }
}
