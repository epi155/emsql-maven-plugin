package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SqlKind {
    void psSet(IndentPrintWriter ipw, String source);
    void xPsPush(IndentPrintWriter ipw, String orig, String name);
    default void psPush(IndentPrintWriter ipw, String name) {
        xPsPush(ipw, "i", name);
    }

    String getPrimitive();
    default String getWrapper() { return getPrimitive(); }
    default String getContainer() { return getWrapper(); }
    default boolean isScalar() { return true; }
    default boolean isNullable() { return false; }

    default Collection<String> requires() { return Set.of(); }
    /*
     * methods for obtaining output values,
     * not usable for extended data types,
     * overridden for standard data types
     */
    default void rsGetValue(IndentPrintWriter ipw, int k) { throw new IllegalStateException(); }
    default void rsGet(IndentPrintWriter ipw, int k, String target) { throw new IllegalStateException(); }
    default void rsPull(IndentPrintWriter ipw, Integer k, String name) { throw new IllegalStateException(); }
    default void registerOut(IndentPrintWriter ipw, int k) { throw new IllegalStateException(); }
    default void csGetValue(IndentPrintWriter ipw, int k) { throw new IllegalStateException(); }
    default void csGet(IndentPrintWriter ipw, int k, String setter) { throw new IllegalStateException(); }
    /*
     * methods for extended data type,
     * not usable for standard data type,
     * overridden for extended data type
     */
    default int columns() { throw new IllegalStateException(); }
    default void setId(int id) { throw new IllegalStateException(); }
    default Map<String, SqlKind> toMap() { throw new IllegalStateException(); }
    default String getGeneric() { throw new IllegalStateException(); }
}
