package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CursorForSelectDynModel;
import io.github.epi155.emsql.api.CursorForSelectModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Map;

public class TdCursorForSelectDyn extends TypeDescription {
    private final CodeFactory factory;

    public TdCursorForSelectDyn(CodeFactory factory) {
        super(CursorForSelectDynModel.class, "!CursorForSelectDyn");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
        substituteProperty("optional-and", Map.class, null, "setOptionalAnd");
    }

    public Object newInstance(Node node) {
        return factory.newCursorForSelectDynModel();
    }
}
