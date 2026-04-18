package io.github.epi155.emsql.api;

import java.util.List;

public interface InsertReturnGeneratedKeysModel extends PerformModel {
    void setOutFields(List<String> fields);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
