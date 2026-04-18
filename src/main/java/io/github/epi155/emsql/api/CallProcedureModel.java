package io.github.epi155.emsql.api;

public interface CallProcedureModel extends PerformModel {
    void setOutput(FieldsModel it);
    void setInputOutput(FieldsModel it);
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
