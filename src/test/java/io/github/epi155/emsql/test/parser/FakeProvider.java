package io.github.epi155.emsql.test.parser;

import io.github.epi155.emsql.spi.ParserProvider;
import io.github.epi155.emsql.spi.SqlParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeProvider implements ParserProvider {
    @Override
    public String name() {
        return "FAKE";
    }

    private static class Helper {
        private static final SqlParser INSTANCE = (query, action) -> {
        };
    }

    @Override
    public SqlParser create() {
        return Helper.INSTANCE;
    }
}
