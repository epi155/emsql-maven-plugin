package io.github.epi155.emsql.api;

public interface InlineProcedureModel extends PerformModel {
    default void setOutput(FieldsModel it) {
        throw new UnsupportedOperationException();
    }

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
