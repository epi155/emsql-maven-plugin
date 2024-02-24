package io.github.epi155.emsql.api;

public interface PerformModel {
    String getExecSql();
    Integer getTimeout();
    boolean isTune();
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);

    default InputModel getInput() { return null; }
    default OutputModel getOutput() { return null; }


    void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException ;
}
