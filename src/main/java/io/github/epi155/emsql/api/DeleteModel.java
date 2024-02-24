package io.github.epi155.emsql.api;

public interface DeleteModel extends PerformModel {
    void setInput(InputModel it);

    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
