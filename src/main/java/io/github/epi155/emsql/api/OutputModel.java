package io.github.epi155.emsql.api;

public interface OutputModel {
    boolean isReflect();
    boolean isDelegate();
    void setReflect(boolean b);
    void setDelegate(boolean b);
}
