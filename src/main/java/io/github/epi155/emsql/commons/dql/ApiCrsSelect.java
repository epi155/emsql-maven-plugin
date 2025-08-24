package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiCrsSelect {
    Integer getFetchSize();
    // SqlAction
    Map<Integer, SqlParam> notScalar(@NotNull Map<Integer, SqlParam> parameters);
    void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlParam> notScalar, String kPrg);
    void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement);
    void setInput(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc);
    void setQueryHints(PrintModel ipw);
    void fetch(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap);
    void openQuery(PrintModel ipw, JdbcStatement jdbc, String kPrg);
}
