package io.github.epi155.emsql.api;

public interface InsertReturnGeneratedKeysModel extends PerformModel {
    void setInput(InputModel it);
    void setOutput(OutFieldsModel it);

    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
