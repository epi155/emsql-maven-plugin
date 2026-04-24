package io.github.epi155.emsql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import io.github.epi155.emsql.test.utils.CompilationTester;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MojoTest
@Slf4j
class TestSingle {

     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jsql.xml")
     void testJ8PojoJSql(SqlMojo mojo) throws Exception {
         log.info("Testing java8/jsql pojo ...");
         mojo.execute();
         
         // Validate generated code compilation
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
     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-jooq.xml")
     void testJ8PojoJooq(SqlMojo mojo) throws Exception {
         log.info("Testing java8/jooq pojo ...");
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
     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-nest.xml")
     void testNestJ8Pojo(SqlMojo mojo) throws Exception {
         log.info("Testing nest java8 pojo ...");
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
     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-xsel.xml")
     void testXSelJ8Pojo(SqlMojo mojo) throws Exception {
         log.info("Testing xSel java8 pojo ...");
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
     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-force.xml")
     void testForceJ8Pojo(SqlMojo mojo) throws Exception {
         log.info("Testing force java8 pojo ...");
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
     @Test
     @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-c-fp.xml")
     void testFpJ8Pojo(SqlMojo mojo) throws Exception {
         log.info("Testing FP cursor java8 pojo ...");
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

    @Test
    @InjectMojo(goal = "generate", pom = "src/test/resources/unit/test-j8/pom-nd.xml")
    void testJ8PojoNoDebug(SqlMojo mojo) throws Exception {
        log.info("Testing java8 pojo (no debug) ...");
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
