package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.plugin.sql.SqlKind;

import java.util.Map;

public class MapContext {
    private final Map<Object, Object> mapping;

    public MapContext(Map<Object, Object> mapping) {
        this.mapping = mapping;
    }

    public SqlKind get(String name) {
        Object kind = mapping.get(name);
        if (kind instanceof SqlKind)
            return (SqlKind) kind;
        return null;
    }
}
