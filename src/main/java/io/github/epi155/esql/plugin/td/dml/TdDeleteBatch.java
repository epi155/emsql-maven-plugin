package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlDeleteBatch;
import org.yaml.snakeyaml.TypeDescription;

public class TdDeleteBatch extends TypeDescription {
    public TdDeleteBatch() {
        super(SqlDeleteBatch.class, "!DeleteBatch");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
