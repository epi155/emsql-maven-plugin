package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiCrsSelect extends DumpAware {
    Integer getFetchSize();
    // SqlAction
    Map<Integer, SqlMulti> notScalar(@NotNull Map<Integer, SqlParam> parameters);
    void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlMulti> notScalar, String kPrg);
    void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement);
    void setInput(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc);
    void setQueryHints(PrintModel ipw);
    void fetch(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap);
    void openQuery(PrintModel ipw, JdbcStatement jdbc, String kPrg);
}
