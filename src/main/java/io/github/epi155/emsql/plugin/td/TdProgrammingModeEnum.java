package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.ProgrammingModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Map;

@Slf4j
public class TdProgrammingModeEnum extends TypeDescription {
    private static final Map<String, ProgrammingModeEnum> PgmMode = Map.ofEntries(
            Map.entry("IP", ProgrammingModeEnum.Imperative),
            Map.entry("IMPERATIVE", ProgrammingModeEnum.Imperative),
            Map.entry("FP", ProgrammingModeEnum.Functional),
            Map.entry("FUNCTIONAL", ProgrammingModeEnum.Functional)
    );

    public TdProgrammingModeEnum() {
        super(ProgrammingModeEnum.class);
    }

    @Override
    public Object newInstance(Node node) {
        // called by ConstructScalar
        ScalarNode sNode = (ScalarNode) node;
        String value = sNode.getValue();
        ProgrammingModeEnum type = PgmMode.get(value.toUpperCase());
        if (type != null) return type;
        log.warn("Invalid cursor programming mode {}, use Imperative", value);
        return ProgrammingModeEnum.Imperative;
    }
}