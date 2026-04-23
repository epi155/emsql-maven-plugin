package io.github.epi155.emsql.api;

public interface TypeModel {
    String getPrimitive();
    default String getterPrefix() {
        return "get";
    }
}
