package io.github.epi155.emsql.api;

public interface SelectSingleModel extends PerformModel {
    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
