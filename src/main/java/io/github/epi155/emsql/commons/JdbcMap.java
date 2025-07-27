package io.github.epi155.emsql.commons;

import java.util.Map;

public interface JdbcMap {
    Map<Integer, SqlParam> getIMap();

    Map<Integer, SqlParam> getOMap();
}
