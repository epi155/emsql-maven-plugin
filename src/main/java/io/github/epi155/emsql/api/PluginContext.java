package io.github.epi155.emsql.api;

import io.github.epi155.emsql.commons.SqlAction;
import io.github.epi155.emsql.commons.SqlParam;

import java.util.Map;

public interface PluginContext {
    boolean isDebug();

    boolean isJava7();

    void incMethods();

    void validate(String query, Class<? extends SqlAction> claz, Map<Integer, SqlParam> parameters);
}
