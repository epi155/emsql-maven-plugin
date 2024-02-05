package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.plugin.ProgrammingModeEnum;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class TdProgrammingModeEnum extends TypeDescription {
    public TdProgrammingModeEnum() {
        super(ProgrammingModeEnum.class);
    }

    @Override
    public Object newInstance(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode sNode = (ScalarNode) node;
            String value = sNode.getValue().toUpperCase();
            switch (value) {
                case "FP":
                case "FUNCTIONAL":
                    return ProgrammingModeEnum.Functional;
                case "IP":
                case "IMPERATIVE":
                    return ProgrammingModeEnum.Imperative;
                default:
                    return null;
            }
        }
        return null;
    }

}
