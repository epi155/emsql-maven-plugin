package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.plugin.sql.SqlKind;

import java.util.Map;
import java.util.stream.Collectors;

public class InvalidSqlParameter extends RuntimeException {
    public InvalidSqlParameter(String parm, Map<String, SqlKind> fields) {
        super("Invalid SQL parameter "+parm+" not in [" + fields.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }
    public InvalidSqlParameter(String parm, Map<String, SqlKind> i, Map<String, SqlKind> o) {
        super("Invalid SQL parameter "+parm+" not in [" +
                i.keySet().stream().sorted().collect(Collectors.joining(",")) + ";"+
                o.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }
}
