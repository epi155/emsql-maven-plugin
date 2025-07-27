package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.commons.SqlParam;

import java.util.Map;

public interface ApiSelectDyn {
    Map<String, String> getOptionalAnd();

    Map<String, Map<Integer, SqlParam>> getAndParms();

    boolean isUnboxRequest(int size);
}
