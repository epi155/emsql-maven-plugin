package io.github.epi155.esql.plugin.td.dql;

import io.github.epi155.esql.plugin.sql.dql.SqlSelectCount;
import org.yaml.snakeyaml.TypeDescription;

public class TdSelectCount extends TypeDescription {
    public TdSelectCount() {
        super(SqlSelectCount.class, "!SelectCount");
        substituteProperty("exec-sql", String.class, null, "setQuery");
        substituteProperty("execSql", String.class, null, "setQuery");
    }
}
