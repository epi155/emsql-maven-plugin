package io.github.epi155.emsql.api;

public interface DeleteBatchModel extends PerformModel {
    void setInput(InputModel it);

    void setBatchSize(int it);

    void setForce(boolean it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
