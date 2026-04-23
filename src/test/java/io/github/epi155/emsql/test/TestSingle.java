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
class TestSingle {

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
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-nest.xml")
    void testNestJ8Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing nest java8 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-xsel.xml")
    void testXSelJ8Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing xSel java8 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-force.xml")
    void testForceJ8Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing force java8 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-c-fp.xml")
    void testFpJ8Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing FP cursor java8 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-nd.xml")
    void testJ8PojoNoDebug(SqlMojo mojo) throws Exception {
        log.info("Testing java8 pojo (no debug) ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }

}
