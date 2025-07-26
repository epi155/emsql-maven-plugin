package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectListDynModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Map;

public class TdSelectListDyn extends TypeDescription {
    private final CodeFactory factory;

    public TdSelectListDyn(CodeFactory factory) {
        super(SelectListDynModel.class, "!SelectListDyn");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
        substituteProperty("optional-and", Map.class, null, "setOptionalAnd");
    }
    public Object newInstance(Node node) {
        return factory.newSelectListDynModel();
    }
}
