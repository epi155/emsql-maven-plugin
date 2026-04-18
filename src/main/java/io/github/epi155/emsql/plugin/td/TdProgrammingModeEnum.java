package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.ProgrammingModeEnum;
import org.yaml.snakeyaml.TypeDescription;

public class TdProgrammingModeEnum extends TypeDescription {

    public TdProgrammingModeEnum() {
        super(ProgrammingModeEnum.class);
    }

    @Override
    public Object newInstance(org.yaml.snakeyaml.nodes.Node node) {
        return ProgrammingModeEnum.Imperative;
    }
}