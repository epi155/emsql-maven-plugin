package io.github.epi155.emsql.api;

import java.util.Collection;
import java.util.Set;

public interface SqlDataType extends TypeModel {

    void psSet(PrintModel ipw, String source);

    void psSet(PrintModel ipw, String source, int k);

    String getPrimitive();

    String getWrapper();

    default boolean isNullable() {
        return false;
    }

    default Collection<String> requires() {
        return Set.of();
    }

    void rsGetValue(PrintModel ipw, int k);

    void csGetValue(PrintModel ipw, int k);

    void registerOut(PrintModel ipw, int k);

    /*
     * methods for extended data type,
     * not usable for standard data type,
     * overridden for extended data type
     */
    default void setId(int id) {
        throw new IllegalStateException();
    }

    default String getGeneric() {
        throw new IllegalStateException();
    }
}
