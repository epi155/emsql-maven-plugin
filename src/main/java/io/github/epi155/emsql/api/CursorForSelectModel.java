package io.github.epi155.emsql.api;

public interface CursorForSelectModel extends PerformModel {
    void setMode(ProgrammingModeEnum it);

    void setFetchSize(Integer it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
