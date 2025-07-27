package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CursorForSelectModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdCursorForSelect extends TypeDescription {
    private final CodeFactory factory;

    public TdCursorForSelect(CodeFactory factory) {
        super(CursorForSelectModel.class, "!CursorForSelect");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }

    public Object newInstance(Node node) {
        return factory.newCursorForSelectModel();
    }
}
