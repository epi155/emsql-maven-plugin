package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.PrintModel;

import java.util.Map;

public interface WhereInAware {
    void expandIn(PrintModel ipw, Map<Integer, SqlParam> parameters, String kPrg);
}
