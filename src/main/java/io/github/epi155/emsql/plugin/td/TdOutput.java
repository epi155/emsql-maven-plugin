package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.OutputModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdOutput extends TypeDescription {
    private final CodeFactory factory;

    public TdOutput(CodeFactory factory) {
        super(OutputModel.class);
        this.factory = factory;
    }

    public Object newInstance(Node node) {
        return factory.newOutputModel();
    }
}
