package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.MethodModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdMethod extends TypeDescription {
    private final CodeFactory factory;

    public TdMethod(CodeFactory factory) {
        super(MethodModel.class);
        this.factory = factory;
    }
    public Object newInstance(Node node) {
        return factory.newMethodModel();
    }
}
