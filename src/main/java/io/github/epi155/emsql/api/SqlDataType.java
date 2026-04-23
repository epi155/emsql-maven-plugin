package io.github.epi155.emsql.api;

import java.util.Collection;
import java.util.Set;

public interface SqlDataType extends TypeModel {
    String getWrapper();
    void psSet(PrintModel ipw, String source);
    void psSet(PrintModel ipw, String source, int k);
    default Collection<String> requires() {
        return Set.of();
    }

}
