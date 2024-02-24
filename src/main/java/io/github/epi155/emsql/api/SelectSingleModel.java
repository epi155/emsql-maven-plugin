package io.github.epi155.emsql.api;

public interface SelectSingleModel extends PerformModel {
    InputModel getInput();
    OutputModel getOutput();
    void setInput(InputModel it);
    void setOutput(OutputModel it);

    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);
}
