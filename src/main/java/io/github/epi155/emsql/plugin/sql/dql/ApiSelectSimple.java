package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.SqlParam;

import java.util.Map;

public interface ApiSelectSimple {
    void declareInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String capitalize);

    void setInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap);

    Integer getTimeout();

    void debugAction(IndentPrintWriter ipw, String kPrg, Map<Integer, SqlParam> iMap, ClassContext cc);

    void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc);

    void declareOutput(IndentPrintWriter ipw, int outSize, ClassContext cc);
}
