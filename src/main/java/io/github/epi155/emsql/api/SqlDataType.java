package io.github.epi155.emsql.api;

import java.util.Collection;
import java.util.Set;

public interface SqlDataType extends TypeModel {
    void psSet(PrintModel ipw, String source);
    void xPsPush(PrintModel ipw, String orig, String name);
    default void psPush(PrintModel ipw, String name) {
        xPsPush(ipw, "i", name);
    }

    String getPrimitive();
    default String getWrapper() { return getPrimitive(); }
    default String getContainer() { return getWrapper(); }
    default boolean isNullable() { return false; }

    default Collection<String> requires() { return Set.of(); }
    /*
     * methods for obtaining output values,
     * not usable for extended data types,
     * overridden for standard data types
     */
    default void rsGetValue(PrintModel ipw, int k) { throw new IllegalStateException(); }
    default void rsGet(PrintModel ipw, int k, String target) { throw new IllegalStateException(); }
    default void rsPull(PrintModel ipw, Integer k, String name) { throw new IllegalStateException(); }
    default void registerOut(PrintModel ipw, int k) { throw new IllegalStateException(); }
    default void csGetValue(PrintModel ipw, int k) { throw new IllegalStateException(); }
    default void csGet(PrintModel ipw, int k, String setter) { throw new IllegalStateException(); }
    /*
     * methods for extended data type,
     * not usable for standard data type,
     * overridden for extended data type
     */
    default void setId(int id) { throw new IllegalStateException(); }
    default String getGeneric() { throw new IllegalStateException(); }
}
