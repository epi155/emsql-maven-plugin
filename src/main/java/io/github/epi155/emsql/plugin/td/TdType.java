package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.TypeModel;
import io.github.epi155.emsql.plugin.SqlMojo;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * Specialized TD class that requires custom instantiation logic.
 * Does not extend TdAbstract due to special handling of ScalarNode.
 */
public class TdType extends TypeDescription {
    private final CodeFactory factory;

    public TdType(CodeFactory factory) {
        super(TypeModel.class);
        this.factory = factory;
    }

    @Override
    public Object newInstance(Node node) {
        // called by ConstructScalar
        ScalarNode sNode = (ScalarNode) node;
        String value = sNode.getValue();
        return factory.getInstance(value, SqlMojo.mapContext.get());
    }
}
