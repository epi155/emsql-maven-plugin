package io.github.epi155.emsql.api;

public interface CommandModel extends PerformModel {
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
