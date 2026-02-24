package io.github.epi155.esql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;

@MojoTest
@Slf4j
public class TestJ7 {

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j7/pom.xml")
    void testJ7Pojo(SqlMojo mojo) throws Exception {
        log.info("Testing java7 pojo ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j7s/pom.xml")
    void testJ7Spring(SqlMojo mojo) throws Exception {
        log.info("Testing java7 spring ...");
        mojo.execute();
        // qui puoi verificare il comportamento, ad esempio con log capturer
    }
}
