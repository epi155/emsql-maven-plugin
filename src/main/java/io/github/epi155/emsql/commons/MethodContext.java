package io.github.epi155.emsql.commons;

public interface MethodContext {
    MethodContext oSize(Integer size);

    MethodContext iSize(Integer size);

    MethodContext nSize(Integer size);

    boolean isInputReflect();

    Integer nSize();

    Integer iSize();

    Integer oSize();

    boolean isInputDelegate();

    boolean isOutputReflect();

    boolean isOutputDelegate();

    String getName();
}
