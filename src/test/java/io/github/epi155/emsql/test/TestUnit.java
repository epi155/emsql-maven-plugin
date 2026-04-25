package io.github.epi155.emsql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import io.github.epi155.emsql.test.utils.CompilationTester;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MojoTest
@Slf4j
class TestUnit {

    @Disabled
    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/sandbox/pom-test.xml")
    void testJ8PojoTest(SqlMojo mojo) throws Exception {
        log.info("Testing java8 fine-tuning ...");
        mojo.execute();

        File generatedDir = getGenerateDirectory(mojo);
        assertTrue(generatedDir.exists(), "Generated directory should exist");

        CompilationTester compilationTester = new CompilationTester();
        CompilationTester.CompilationResult result = compilationTester.compileWithDiagnostics(generatedDir);

        log.info("Compilation diagnostics: {}", result.getMessage());
        if (!result.isSuccess()) {
            log.error("Compilation failed with diagnostics: {}", String.join("\n", result.getDiagnostics()));
        }
        assertTrue(result.isSuccess(), "Compilation should succeed: " + result.getMessage());
    }

    private File getGenerateDirectory(SqlMojo mojo) {
        try {
            java.lang.reflect.Field field = SqlMojo.class.getDeclaredField("generateDirectory");
            field.setAccessible(true);
            return (File) field.get(mojo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access generateDirectory field", e);
        }
    }
}
