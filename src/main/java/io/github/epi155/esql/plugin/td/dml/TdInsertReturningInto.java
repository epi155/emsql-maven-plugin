package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlInsertReturningInto;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsertReturningInto extends TypeDescription {
    public TdInsertReturningInto() {
        super(SqlInsertReturningInto.class, "!InsertReturnInto");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
