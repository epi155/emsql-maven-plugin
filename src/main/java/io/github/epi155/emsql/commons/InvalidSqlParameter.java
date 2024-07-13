package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.SqlDataType;

import java.util.Map;
import java.util.stream.Collectors;

public class InvalidSqlParameter extends RuntimeException {
    private static final String INVALID_SQL_PARAMETER = "Invalid SQL parameter ";
    public InvalidSqlParameter(String parm, Map<String, SqlDataType> fields) {
        super(INVALID_SQL_PARAMETER+parm+" not in [" + fields.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }
    public InvalidSqlParameter(String parm, Map<String, SqlDataType> i, Map<String, SqlDataType> o) {
        super(INVALID_SQL_PARAMETER+parm+" not in [" +
                i.keySet().stream().sorted().collect(Collectors.joining(",")) + ";"+
                o.keySet().stream().sorted().collect(Collectors.joining(",")) + "]");
    }

    public InvalidSqlParameter(String parm) {
        super(INVALID_SQL_PARAMETER+parm+" in this context (list in batch operation) ");
    }
}
