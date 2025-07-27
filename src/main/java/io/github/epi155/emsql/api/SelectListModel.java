package io.github.epi155.emsql.api;

public interface SelectListModel extends PerformModel {
    void setInput(InputModel it);

    void setOutput(OutputModel it);

    void setFetchSize(Integer it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
