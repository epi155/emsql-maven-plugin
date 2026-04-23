package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.DumpAware;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlOutParam;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;

import java.util.Map;

public interface ApiWrite extends ApiDocSignature, DumpAware {
    void declareInput(PrintModel ipw, JdbcStatement jdbc);

    void declareOutput(PrintModel ipw);

    void setInputAbs(PrintModel ipw, JdbcStatement jdbc);

    void registerOutAbs(PrintModel ipw, Map<Integer, SqlOutParam> oMap);

    void setQueryHints(PrintModel ipw);

    void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbc);

    void getOutput(PrintModel ipw, Map<Integer, SqlOutParam> oMap);
}
