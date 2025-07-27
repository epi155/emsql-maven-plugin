package io.github.epi155.emsql.spi;

public interface ParserProvider {
    String name();

    SqlParser create();
}
