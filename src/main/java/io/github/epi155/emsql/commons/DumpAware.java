package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;

public interface DumpAware {
    void dumpAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement);
}
