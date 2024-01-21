package io.github.epi155.esql.plugin.td.dml;

import io.github.epi155.esql.plugin.sql.dml.SqlInsert;
import org.yaml.snakeyaml.TypeDescription;

public class TdInsert extends TypeDescription {
    public TdInsert() {
        super(SqlInsert.class, "!Insert");
    }
}
