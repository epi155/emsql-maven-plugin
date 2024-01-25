package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlUpdate;
import org.yaml.snakeyaml.TypeDescription;

public class TdUpdate extends TypeDescription {
    public TdUpdate() {
        super(SqlUpdate.class, "!Update");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
