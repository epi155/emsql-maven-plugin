package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;

import java.util.Map;

public class InvalidSqlParameter extends RuntimeException {
    public InvalidSqlParameter(String parm, Map<String, SqlEnum> fields) {
        super("Invalid SQL parameter "+parm+" not in [" + String.join(",", fields.keySet()) + "]");
    }
    public InvalidSqlParameter(String parm, Map<String, SqlEnum> i, Map<String, SqlEnum> o) {
        super("Invalid SQL parameter "+parm+" not in [" +
                String.join(",", i.keySet()) + ";"+
                String.join(",", o.keySet()) + "]");
    }
}
