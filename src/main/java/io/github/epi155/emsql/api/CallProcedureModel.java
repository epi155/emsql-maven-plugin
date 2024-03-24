package io.github.epi155.emsql.api;

public interface CallProcedureModel extends PerformModel {
    void setInput(InputModel it);
    void setOutput(OutFieldsModel it);
    void setInputOutput(InOutFieldsModel it);

    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
