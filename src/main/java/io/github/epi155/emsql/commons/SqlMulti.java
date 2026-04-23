package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.SqlVectorType;

public class SqlMulti extends SqlParam {
    public SqlMulti(String name, SqlVectorType type) {
        super(name, type);
    }
    public SqlVectorType getType() {
        return (SqlVectorType) super.getType();
    }
}
