package io.github.epi155.emsql.test.parser;

import io.github.epi155.emsql.spi.ParserProvider;
import io.github.epi155.emsql.spi.SqlParser;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

@Slf4j
public class JSqlProvider implements ParserProvider {
    @Override
    public String name() {
        return "JSQL";
    }

    private static class Helper {
        private static final SqlParser INSTANCE = (query, action) -> {
            if (action.isAssignableFrom(io.github.epi155.emsql.pojo.dpl.SqlInlineProcedure.class)) return;
            if (action.isAssignableFrom(io.github.epi155.emsql.pojo.dpl.SqlInlineBatch.class)) return;
            if (action.isAssignableFrom(io.github.epi155.emsql.pojo.dpl.SqlCommand.class)) return;

            if (action.isAssignableFrom(io.github.epi155.emsql.spring.dpl.SqlInlineProcedure.class)) return;
            if (action.isAssignableFrom(io.github.epi155.emsql.spring.dpl.SqlInlineBatch.class)) return;
            if (action.isAssignableFrom(io.github.epi155.emsql.spring.dpl.SqlCommand.class)) return;

            try {
                CCJSqlParserUtil.parse(query);
            } catch (JSQLParserException e) {
                log.error("Invalid statement: {}", query);
                throw new IllegalArgumentException(e);
            }
        };
    }

    @Override
    public SqlParser create() {
        return Helper.INSTANCE;
    }
}
