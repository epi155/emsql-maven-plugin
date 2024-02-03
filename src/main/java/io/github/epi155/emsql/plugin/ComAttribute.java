package io.github.epi155.emsql.plugin;

public interface ComAttribute {
    boolean isReflect();
    default boolean isDelegate() { return false; }
}
