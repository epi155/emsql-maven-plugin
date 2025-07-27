package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.SelectListModel;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

public class TdSelectList extends TypeDescription {
    private final CodeFactory factory;

    public TdSelectList(CodeFactory factory) {
        super(SelectListModel.class, "!SelectList");
        this.factory = factory;

        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }

    public Object newInstance(Node node) {
        return factory.newSelectListModel();
    }
}
