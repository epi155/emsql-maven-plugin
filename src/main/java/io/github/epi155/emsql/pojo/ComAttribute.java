package io.github.epi155.emsql.pojo;

public interface ComAttribute {
    boolean isReflect();
    default boolean isDelegate() { return false; }
}
