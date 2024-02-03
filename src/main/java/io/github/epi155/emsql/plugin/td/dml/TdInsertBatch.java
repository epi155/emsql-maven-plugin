package io.github.epi155.emsql.plugin.td.dml;

import io.github.epi155.emsql.plugin.sql.dml.SqlInsertBatch;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsertBatch extends TypeDescription {
    public TdInsertBatch() {
        super(SqlInsertBatch.class, "!InsertBatch");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
