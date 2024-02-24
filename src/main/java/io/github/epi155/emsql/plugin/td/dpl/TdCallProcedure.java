package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.api.CallProcedureModel;
import io.github.epi155.emsql.api.CodeFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdCallProcedure extends TypeDescription {
    private final CodeFactory factory;

    public TdCallProcedure(CodeFactory factory) {
        super(CallProcedureModel.class, "!CallProcedure");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
    public Object newInstance(Node node) {
        return factory.newCallProcedureModel();
    }
}
