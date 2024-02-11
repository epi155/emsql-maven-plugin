package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.InputAware;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.WhereInAware;

import java.util.List;

public interface ApiWriteMethod extends InputAware, WhereInAware {
    void docBegin(IndentPrintWriter ipw);
    void docInput(IndentPrintWriter ipw, JdbcStatement jdbc);
    void docEnd(IndentPrintWriter ipw);
    void declareGenerics(IndentPrintWriter ipw, String cName, List<String> in);
    void declareInput(IndentPrintWriter ipw, JdbcStatement jdbc);
    void setQueryHints(IndentPrintWriter ipw);
    void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbc);
}
