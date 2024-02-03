package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.SqlParam;

import java.util.Map;

public interface ApiSelectSignature {
    void docBegin(IndentPrintWriter ipw);

    void docInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap);

    void docOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap);

    void docEnd(IndentPrintWriter ipw);

    void declareGenerics(IndentPrintWriter ipw, String cName, int iSize, int oSize);
}
