package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiSelectDyn extends DumpAware {
    Map<String, String> getOptionalAnd();

    Map<String, Map<Integer, SqlParam>> getAndParms();

    boolean isUnboxRequest(int size);
    Integer getFetchSize();

    Map<Integer, SqlMulti> notScalar(@NotNull Map<Integer, SqlParam> parameters);
    void setInput(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc);
    void setQueryHints(PrintModel ipw);
    void fetch(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap);
}
