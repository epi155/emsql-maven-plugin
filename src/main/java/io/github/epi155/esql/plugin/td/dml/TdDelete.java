package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlDelete;
import org.yaml.snakeyaml.TypeDescription;

public class TdDelete extends TypeDescription {
    public TdDelete() {
        super(SqlDelete.class, "!Delete");
    }
}
