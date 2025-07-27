package io.github.epi155.emsql.api;

import java.util.Map;

public interface SelectListDynModel extends SelectListModel {
    void setInput(InputModel it);

    void setOutput(OutputModel it);

    void setFetchSize(Integer it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);

    void setOptionalAnd(Map<String, String> it);
}
