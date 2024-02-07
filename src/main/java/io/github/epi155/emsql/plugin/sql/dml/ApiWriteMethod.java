package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;

public interface ApiWriteMethod {
    void docBegin(IndentPrintWriter ipw);

    void docInput(IndentPrintWriter ipw, JdbcStatement jdbc);

    void docEnd(IndentPrintWriter ipw);

    void declareGenerics(IndentPrintWriter ipw, String cName, int iSize, int i);

    void declareInput(IndentPrintWriter ipw, JdbcStatement jdbc, ClassContext cc);

    void setInput(IndentPrintWriter ipw, JdbcStatement jdbc);

    void setQueryHints(IndentPrintWriter ipw);

    void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbc, ClassContext cc);
}
