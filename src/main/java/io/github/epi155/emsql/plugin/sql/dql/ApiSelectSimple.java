package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ApiSelectSimple {
    void declareInput(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc);

    void setInput(IndentPrintWriter ipw, JdbcStatement jdbc);

    Integer getTimeout();

    void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbc, ClassContext cc);

    void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc);

    void declareOutput(IndentPrintWriter ipw, int outSize, ClassContext cc);
}
