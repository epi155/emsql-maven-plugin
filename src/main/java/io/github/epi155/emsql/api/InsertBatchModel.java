package io.github.epi155.emsql.api;

public interface InsertBatchModel extends PerformModel {
    void setInput(InputModel it);
    void setBatchSize(int it);

    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
