package io.github.epi155.emsql.api;

public interface MethodModel {
    String getMethodName();

    void setMethodName(String it);

    PerformModel getPerform();

    void setPerform(PerformModel it);

    void writeCode(PrintModel pm, int count) throws InvalidQueryException;
}
