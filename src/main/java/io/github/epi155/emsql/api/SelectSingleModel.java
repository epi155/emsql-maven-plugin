package io.github.epi155.emsql.api;

public interface SelectSingleModel extends PerformModel {
    InputModel getInput();

    void setInput(InputModel it);

    OutputModel getOutput();

    void setOutput(OutputModel it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
