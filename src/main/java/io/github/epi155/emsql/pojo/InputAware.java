package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.PrintModel;

import java.util.Map;

public interface InputAware {
    void setInput(PrintModel ipw, JdbcStatement jdbc);

    Map<Integer, SqlParam> notScalar(Map<Integer, SqlParam> parameters);
}
