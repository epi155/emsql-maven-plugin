package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;

import java.util.Collection;
import java.util.Set;

public interface SqlKind {
    void psSet(IndentPrintWriter ipw, String source, ClassContext cc);
    void psPush(IndentPrintWriter ipw, String name, ClassContext cc);
    default void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) { throw new IllegalStateException(); }
    default void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) { throw new IllegalStateException(); }
    default void rsPull(IndentPrintWriter ipw, Integer k, String name) { throw new IllegalStateException(); }
    default void registerOut(IndentPrintWriter ipw, int k) { throw new IllegalStateException(); }
    default void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) { throw new IllegalStateException(); }
    default void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) { throw new IllegalStateException(); }
    default Collection<String> requires() { return Set.of(); }
    default String getPrimitive() { throw new IllegalStateException(); }
    default String getWrapper() { throw new IllegalStateException(); }
    default boolean isScalar() { return true; }
    default int columns() { throw new IllegalStateException(); }
}
