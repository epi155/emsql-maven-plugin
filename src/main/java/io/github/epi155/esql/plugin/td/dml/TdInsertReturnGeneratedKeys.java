package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlInsertReturnGeneratedKeys;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsertReturnGeneratedKeys extends TypeDescription {
    public TdInsertReturnGeneratedKeys() {
        super(SqlInsertReturnGeneratedKeys.class, "!InsertReturnKeys");
    }
}
