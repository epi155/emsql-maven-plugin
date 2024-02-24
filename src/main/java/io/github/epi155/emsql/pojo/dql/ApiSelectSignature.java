package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlParam;

import java.util.List;
import java.util.Map;

public interface ApiSelectSignature {
    void docBegin(PrintModel ipw);

    void docInput(PrintModel ipw, JdbcStatement jdbc);

    void docOutput(PrintModel ipw, Map<Integer, SqlParam> oMap);

    void docEnd(PrintModel ipw);

    void declareGenerics(PrintModel ipw, String cName, List<String> in);
}
