package io.github.epi155.emsql.api;

public interface MethodModel {
    String getMethodName();
    PerformModel getPerform();

    void setMethodName(String it);
    void setPerform(PerformModel it);

    void writeCode(PrintModel pm, int count) throws InvalidQueryException;
}
