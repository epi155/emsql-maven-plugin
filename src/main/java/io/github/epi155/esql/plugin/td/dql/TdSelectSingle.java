package io.github.epi155.esql.plugin.td.dql;

import io.github.epi155.esql.plugin.sql.dql.SqlSelectSingle;
import org.yaml.snakeyaml.TypeDescription;

public class TdSelectSingle extends TypeDescription {
    public TdSelectSingle() {
        super(SqlSelectSingle.class, "!SelectSingle");
        substituteProperty("exec-sql", String.class, null, "setQuery");
    }
}
