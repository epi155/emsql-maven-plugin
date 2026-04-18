package io.github.epi155.emsql.api;

public interface SelectOptionalModel extends SelectSingleModel {
    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
