package io.github.epi155.esql.plugin.td.dql;

import io.github.epi155.esql.plugin.sql.dql.SqlCursorForUpdate;
import org.yaml.snakeyaml.TypeDescription;

public class TdCursorForUpdate extends TypeDescription {
    public TdCursorForUpdate() {
        super(SqlCursorForUpdate.class, "!CursorForUpdate");
    }
}
