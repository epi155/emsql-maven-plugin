package io.github.epi155.emsql.api;

import java.util.Map;

public interface CursorForSelectDynModel extends CursorForSelectModel {
    void setMode(ProgrammingModeEnum it);

    void setFetchSize(Integer it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);

    void setOptionalAnd(Map<String, String> it);
}
