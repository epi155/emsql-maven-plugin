package io.github.epi155.esql.plugin;

public interface ComAttribute {
    boolean isReflect();
    default boolean isDelegate() { return false; }
}
