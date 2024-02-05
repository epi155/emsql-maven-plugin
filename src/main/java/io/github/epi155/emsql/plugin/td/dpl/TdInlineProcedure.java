package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.plugin.sql.dpl.SqlInlineProcedure;
import org.yaml.snakeyaml.TypeDescription;

public class TdInlineProcedure extends TypeDescription {
    public TdInlineProcedure() {
        super(SqlInlineProcedure.class, "!InlineProcedure");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
