package io.github.epi155.emsql.api;

public interface PrintModel {
    void printf(String format, Object... objects);

    void putf(String format, Object... objects);

    void more();

    void println();

    void less();

    void ends();

    void orElse();

    void commaLn();

    void closeParenthesisLn();
}
