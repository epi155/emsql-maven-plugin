package io.github.epi155.esql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;

@MojoTest
@Slf4j
class TestJ8 {

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom.xml")
    void testJ8Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing java8 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8s/pom.xml")
    void testJ8Spring(SqlMojo mojo) throws Exception {
        log.info("Testing java8 spring ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }

}
