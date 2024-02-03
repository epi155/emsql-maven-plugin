package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.plugin.sql.dql.SqlSelectList;
import org.yaml.snakeyaml.TypeDescription;

public class TdSelectList extends TypeDescription {
    public TdSelectList() {
        super(SqlSelectList.class, "!SelectList");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }
}