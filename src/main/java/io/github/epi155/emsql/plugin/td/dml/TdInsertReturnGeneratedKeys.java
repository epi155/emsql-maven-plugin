package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.plugin.sql.dml.SqlInsertReturnGeneratedKeys;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsertReturnGeneratedKeys extends TypeDescription {
    public TdInsertReturnGeneratedKeys() {
        super(SqlInsertReturnGeneratedKeys.class, "!InsertReturnKeys");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
