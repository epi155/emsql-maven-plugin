package io.github.epi155.emsql.api;

public interface SelectOptionalModel extends PerformModel {
    InputModel getInput();

    void setInput(InputModel it);

    OutputModel getOutput();

    void setOutput(OutputModel it);

    void setExecSql(String it);

    void setTimeout(Integer it);

    void setTune(boolean it);
}
