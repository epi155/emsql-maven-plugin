package io.github.epi155.emsql.api;

public interface PerformModel {
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);

    void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException;
}
