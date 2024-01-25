package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlUpdateBatch;
import org.yaml.snakeyaml.TypeDescription;

public class TdUpdateBatch extends TypeDescription {
    public TdUpdateBatch() {
        super(SqlUpdateBatch.class, "!UpdateBatch");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("batch-size", int.class, null, "setBatchSize");
    }
}
