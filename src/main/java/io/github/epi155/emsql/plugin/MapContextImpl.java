package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.MapContext;
import io.github.epi155.emsql.api.TypeModel;

import java.util.Map;

public class MapContextImpl implements MapContext {
    private final Map<Object, Object> mapping;

    public MapContextImpl(Map<Object, Object> mapping) {
        this.mapping = mapping;
    }

    @Override
    public TypeModel get(String name) {
        Object kind = mapping.get(name);
        if (kind instanceof TypeModel)
            return (TypeModel) kind;
        return null;
    }
}
