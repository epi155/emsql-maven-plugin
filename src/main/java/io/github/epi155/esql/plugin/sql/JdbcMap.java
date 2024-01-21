package io.github.epi155.esql.plugin.sql;

import java.util.Map;

public interface JdbcMap {
    Map<Integer, SqlParam> getIMap();
    Map<Integer, SqlParam> getOMap();
}
