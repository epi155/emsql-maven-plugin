package io.github.epi155.emsql.plugin.td.dpl;

import io.github.epi155.emsql.plugin.sql.dpl.SqlCallProcedure;
import org.yaml.snakeyaml.TypeDescription;

public class TdCallProcedure extends TypeDescription {
    public TdCallProcedure() {
        super(SqlCallProcedure.class, "!CallProcedure");
        substituteProperty("exec-sql", String.class, null, "setExecSql");
    }
}
