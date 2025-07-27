package io.github.epi155.emsql.api;

public interface PerformModel {
    String getExecSql();

    void setExecSql(String it);

    Integer getTimeout();

    void setTimeout(Integer it);

    boolean isTune();

    void setTune(boolean it);

    InputModel getInput();

    OutputModel getOutput();


    void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException;
}
