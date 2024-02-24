package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.SqlDataType;

import java.util.Map;
import java.util.stream.Collectors;

public class InvalidSqlParameter extends RuntimeException {
    public InvalidSqlParameter(String parm, Map<String, SqlDataType> fields) {
        super("Invalid SQL parameter "+parm+" not in [" + fields.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }
    public InvalidSqlParameter(String parm, Map<String, SqlDataType> i, Map<String, SqlDataType> o) {
        super("Invalid SQL parameter "+parm+" not in [" +
                i.keySet().stream().sorted().collect(Collectors.joining(",")) + ";"+
                o.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }
}
