package io.github.epi155.emsql.commons.dml;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.InputAware;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.WhereInAware;

import java.util.List;

public interface ApiWriteMethod extends InputAware, WhereInAware {
    void docBegin(PrintModel ipw);
    void docInput(PrintModel ipw, JdbcStatement jdbc);
    void docEnd(PrintModel ipw);
    void declareGenerics(PrintModel ipw, String cName, List<String> in);
    void declareInput(PrintModel ipw, JdbcStatement jdbc);
    void setQueryHints(PrintModel ipw);
    void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbc);
}
