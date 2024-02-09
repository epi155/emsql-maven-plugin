package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;

import java.util.Map;

public interface InputAware {
    void setInput(IndentPrintWriter ipw, JdbcStatement jdbc, ClassContext cc);

    Map<Integer, SqlParam> notScalar(Map<Integer, SqlParam> parameters);
}
