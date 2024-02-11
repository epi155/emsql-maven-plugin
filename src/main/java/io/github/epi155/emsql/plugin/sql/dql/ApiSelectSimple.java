package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.InputAware;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import io.github.epi155.emsql.plugin.sql.WhereInAware;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiSelectSimple extends InputAware, WhereInAware {
    void declareInput(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc);
    void setQueryHints(IndentPrintWriter ipw);
    void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbc);
    void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap);
    void declareOutput(IndentPrintWriter ipw);
}
