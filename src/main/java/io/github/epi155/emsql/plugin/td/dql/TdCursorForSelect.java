package io.github.epi155.emsql.plugin.td.dql;

import io.github.epi155.emsql.plugin.sql.dql.SqlCursorForSelect;
import org.yaml.snakeyaml.TypeDescription;

public class TdCursorForSelect extends TypeDescription {
    public TdCursorForSelect() {
        super(SqlCursorForSelect.class, "!CursorForSelect");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteProperty("fetch-size", Integer.class, null, "setFetchSize");
    }
}