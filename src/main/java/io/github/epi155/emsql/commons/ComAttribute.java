package io.github.epi155.emsql.commons;

public interface ComAttribute {
    boolean isReflect();
    default boolean isDelegate() { return false; }
}
