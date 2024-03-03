package io.github.epi155.emsql.api;

public interface PerformModel {
    String getExecSql();
    Integer getTimeout();
    boolean isTune();
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);

    InputModel getInput();
    OutputModel getOutput();


    void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException ;
}
