package io.github.epi155.esql.plugin.sql.dml;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.sql.SqlParam;

import java.util.Map;

public interface ApiWriteMethod {
    void docBegin(IndentPrintWriter ipw);

    void docInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap);

    void docEnd(IndentPrintWriter ipw);

    void declareGenerics(IndentPrintWriter ipw, String cName, int iSize, int i);

    void declareInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String cName);

    void setInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap);

    Integer getTimeout();

    void debugAction(IndentPrintWriter ipw, String kPrg, Map<Integer, SqlParam> iMap, ClassContext cc);
}
