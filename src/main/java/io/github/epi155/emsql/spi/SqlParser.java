package io.github.epi155.emsql.spi;

import io.github.epi155.emsql.commons.SqlAction;

public interface SqlParser {
    void validate(String query, Class<? extends SqlAction> action);
}
