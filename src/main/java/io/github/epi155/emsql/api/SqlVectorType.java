package io.github.epi155.emsql.api;

import java.util.Map;

public interface SqlVectorType extends SqlDataType {
    void setId(int id);

    String getGeneric();

    int columns();

    Map<String, SqlDataType> toMap();
}
