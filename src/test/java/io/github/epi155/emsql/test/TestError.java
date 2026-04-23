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

import java.nio.file.ProviderNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MojoTest
@Slf4j
class TestError {

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-jsql-ko.xml")
    void testJ8PojoJSqlKo(SqlMojo mojo) {
        log.info("Testing java8/jsql/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseInstanceOf(ParseException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-jooq-ko.xml")
    void testJ8PojoJooqKo(SqlMojo mojo) {
        log.info("Testing java8/jooq/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(ParserException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-err.xml")
    void testJ8PojoErr(SqlMojo mojo) {
        log.info("Testing java8/error pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-inv.xml")
    void testJ8PojoInv(SqlMojo mojo) {
        log.info("Testing java8/invalid pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidSqlParameter.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-def.xml")
    void testJ8PojoDef(SqlMojo mojo) {
        log.info("Testing java8/declare pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(ConstructorException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-in-err.xml")
    void testJ8PojoInKo(SqlMojo mojo) {
        log.info("Testing java8/IN*Batch pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidSqlParameter.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-d-syntax.xml")
    void testJ8PojoDelKo(SqlMojo mojo) {
        log.info("Testing java8/del/synt pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-i-syntax.xml")
    void testJ8PojoInsKo(SqlMojo mojo) {
        log.info("Testing java8/ins/synt pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-u-syntax.xml")
    void testJ8PojoUpdKo(SqlMojo mojo) {
        log.info("Testing java8/upd/synt pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-no-mod.xml")
    void testJ8PojoNoMod(SqlMojo mojo) {
        log.info("Testing java8/no-modules pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-bad-p1.xml")
    void testJ8PojoBadCodeProvider(SqlMojo mojo) {
        log.info("Testing java8/bed-code-prov pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(ProviderNotFoundException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-proc-bad.xml")
    void testJ8PojoProcKo(SqlMojo mojo) {
        log.info("Testing java8/bad-proc pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-proc-bad2.xml")
    void testJ8PojoProcKo2(SqlMojo mojo) {
        log.info("Testing java8/bad2-proc pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-err/pom-proc2-bad.xml")
    void testJ8PojoProc2Ko(SqlMojo mojo) {
        log.info("Testing java8/bad-proc2 pojo ...");
        assertThatThrownBy(mojo::execute)
                .isInstanceOf(MojoExecutionException.class)
                .hasCauseInstanceOf(InvalidQueryException.class);
    }
}
