package io.github.epi155.emsql.test.parser;

import io.github.epi155.emsql.commons.SqlAction;
import io.github.epi155.emsql.spi.ParserProvider;
import io.github.epi155.emsql.spi.SqlParser;
import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.ParserException;

@Slf4j
public class JooqProvider implements ParserProvider {
    @Override
    public String name() {
        return "JOOQ";
    }

    private static class Helper {
        private static final SqlParser INSTANCE = new SqlParser() {
            private final org.jooq.Parser jooqParser = new DefaultDSLContext(SQLDialect.DEFAULT).parser();
            @Override
            public void validate(String query, Class<? extends SqlAction> action) {
                if (action.isAssignableFrom(io.github.epi155.emsql.pojo.dpl.SqlInlineProcedure.class)) return;
                if (action.isAssignableFrom(io.github.epi155.emsql.pojo.dpl.SqlCallProcedure.class)) return;

                if (action.isAssignableFrom(io.github.epi155.emsql.spring.dpl.SqlInlineProcedure.class)) return;
                if (action.isAssignableFrom(io.github.epi155.emsql.spring.dpl.SqlCallProcedure.class)) return;

                try {
                    jooqParser.parse(query);
                } catch (ParserException e) {
                    log.error("Invalid statement: {}", query);
                    throw e;
                }
            }
        };
    }

    @Override
    public SqlParser create() {
        return Helper.INSTANCE;
    }
}
