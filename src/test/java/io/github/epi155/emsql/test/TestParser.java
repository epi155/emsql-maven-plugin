package io.github.epi155.emsql.test;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.commons.InvalidSqlParameter;
import io.github.epi155.emsql.plugin.SqlMojo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.ParseException;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.jooq.impl.ParserException;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.constructor.ConstructorException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MojoTest
@Slf4j
class TestParser {

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jsql.xml")
    void testJ8PojoJSql(SqlMojo mojo) throws Exception {
        log.info("Testing java8/jsql pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jooq.xml")
    void testJ8PojoJooq(SqlMojo mojo) throws Exception {
        log.info("Testing java8/jooq pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jsql-ko.xml")
    void testJ8PojoJSqlKo(SqlMojo mojo) {
        log.info("Testing java8/jsql/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseInstanceOf(ParseException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jooq-ko.xml")
    void testJ8PojoJooqKo(SqlMojo mojo) {
        log.info("Testing java8/jooq/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(ParserException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-err.xml")
    void testJ8PojoErr(SqlMojo mojo) {
        log.info("Testing java8/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-inv.xml")
    void testJ8PojoInv(SqlMojo mojo) {
        log.info("Testing java8/invalid pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidSqlParameter.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-def.xml")
    void testJ8PojoDef(SqlMojo mojo) {
        log.info("Testing java8/declare pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(ConstructorException.class);
    }
}
