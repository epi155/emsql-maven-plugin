package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.InputAware;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.WhereInAware;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiSelectSimple extends InputAware, WhereInAware {
    void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc);

    void setQueryHints(PrintModel ipw);

    void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbc);

    void fetch(PrintModel ipw, Map<Integer, SqlParam> oMap);

    void declareOutput(PrintModel ipw);
}
