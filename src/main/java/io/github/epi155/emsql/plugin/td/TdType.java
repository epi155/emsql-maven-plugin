package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.TypeModel;
import io.github.epi155.emsql.plugin.SqlMojo;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class TdType extends TypeDescription {
    private final CodeFactory factory;

    public TdType(CodeFactory factory) {
        super(TypeModel.class);
        this.factory = factory;
    }
    @Override
    public Object newInstance(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode sNode = (ScalarNode) node;
            String value = sNode.getValue();
            return factory.getInstance(value, SqlMojo.mapContext.get());
        }
        return null;
    }
}
