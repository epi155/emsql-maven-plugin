package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.plugin.sql.dml.SqlDelete;
import org.yaml.snakeyaml.TypeDescription;

public class TdDelete extends TypeDescription {
    public TdDelete() {
        super(SqlDelete.class, "!Delete");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}