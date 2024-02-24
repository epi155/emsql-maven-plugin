package io.github.epi155.emsql.api;

import java.util.Map;

public interface TypeModel {
    default int columns() { throw new IllegalStateException(); }

    default <T extends TypeModel> Map<String, T> toMap() { throw new IllegalStateException(); }

    String getWrapper();

    String getPrimitive();

    default String getterPrefix() { return "get"; }

    boolean isNullable();

    default boolean isScalar() { return true; }
}
