package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.plugin.sql.dql.SqlSelectOptional;
import org.yaml.snakeyaml.TypeDescription;

public class TdSelectOptional extends TypeDescription {
    public TdSelectOptional() {
        super(SqlSelectOptional.class, "!SelectOptional");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}