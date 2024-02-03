package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.plugin.sql.dml.SqlInsert;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsert extends TypeDescription {
    public TdInsert() {
        super(SqlInsert.class, "!Insert");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
