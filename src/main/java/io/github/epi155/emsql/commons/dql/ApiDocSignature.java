package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlOutParam;

import java.util.List;
import java.util.Map;

public interface ApiDocSignature {
    void docBegin(PrintModel ipw);

    void docInput(PrintModel ipw, JdbcStatement jdbc);

    void docOutput(PrintModel ipw, Map<Integer, SqlOutParam> oMap);

    void docEnd(PrintModel ipw);

    void declareGenerics(PrintModel ipw, List<String> in, String iName, String oName);
}
