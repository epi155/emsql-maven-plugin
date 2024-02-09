package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;

import java.util.Map;

public interface WhereInAware {
    void expandIn(IndentPrintWriter ipw, Map<Integer, SqlParam> parameters, String kPrg, int nSize, ClassContext cc);
}
