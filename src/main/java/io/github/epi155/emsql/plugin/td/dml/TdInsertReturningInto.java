package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.plugin.sql.dml.SqlInsertReturningInto;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsertReturningInto extends TypeDescription {
    public TdInsertReturningInto() {
        super(SqlInsertReturningInto.class, "!InsertReturnInto");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
