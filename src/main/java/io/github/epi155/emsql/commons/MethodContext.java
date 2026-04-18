package io.github.epi155.emsql.commons;

import java.util.Optional;

public interface MethodContext extends InputMask, OutputMask {
    MethodContext oSize(Integer size);

    MethodContext iSize(Integer size);

    MethodContext nSize(Integer size);

    Integer nSize();

    Integer iSize();

    Integer oSize();

    String getName();

    Optional<String> iFind(String name);

    void iRegister(String name, String result);

    Optional<String> oFind(String name);

    void oRegister(String name, String result);
}