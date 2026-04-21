package io.github.epi155.emsql.test.regression;

import io.github.epi155.emsql.plugin.SqlMojo;
import io.github.epi155.emsql.test.utils.CompilationTester;
import io.github.epi155.emsql.test.utils.GeneratedCodeValidator;
import io.github.epi155.emsql.test.utils.SnapshotManager;
import io.github.epi155.emsql.test.utils.TestResourceManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DqlRegressionTest {

    private static final String DQL_DIR = "src/test/resources/regression/dql";
    private static final String DML_DIR = "src/test/resources/regression/dml";
    private SnapshotManager snapshotManager;
    private final CompilationTester compilationTester = new CompilationTester();

    @BeforeEach
    void setUp() {
        snapshotManager = new SnapshotManager("src/test/resources/snapshots", false);
    }

    static Stream<String> selectSingleTestCases() {
        return Stream.of(
            "select-single/daoInt-selectSingle",
            "select-single/daoBigint-selectSingle",
            "select-single/daoVarchar-selectSingle",
            "select-single/daoDate-selectSingle"
        );
    }

    static Stream<String> selectOptionalTestCases() {
        return Stream.of(
            "select-optional/daoInt-selectOptional",
            "select-optional/daoBigint-selectOptional",
            "select-optional/daoVarchar-selectOptional",
            "select-optional/daoDate-selectOptional"
        );
    }

    static Stream<String> selectListTestCases() {
        return Stream.of(
            "select-list/daoInt-selectList",
            "select-list/daoBigint-selectList"
        );
    }

    static Stream<String> cursorTestCases() {
        return Stream.of(
            "cursor/daoInt-cursor"
        );
    }

    static Stream<String> insertTestCases() {
        return Stream.of(
            "insert/daoInt-insert",
            "insert/daoBigint-insert",
            "insert/daoVarchar-insert",
            "insert/daoDate-insert"
        );
    }

    static Stream<String> updateTestCases() {
        return Stream.of(
            "update/daoInt-update",
            "update/daoBigint-update",
            "update/daoVarchar-update",
            "update/daoDate-update"
        );
    }

    static Stream<String> deleteTestCases() {
        return Stream.of(
            "delete/daoInt-delete",
            "delete/daoBigint-delete"
        );
    }

    // Java8 POJO tests (default)
    @ParameterizedTest
    @MethodSource("selectSingleTestCases")
    void testSelectSingleRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("selectOptionalTestCases")
    void testSelectOptionalRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("selectListTestCases")
    void testSelectListRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("cursorTestCases")
    void testCursorRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    void testInsertRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("updateTestCases")
    void testUpdateRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, false);
    }

    @ParameterizedTest
    @MethodSource("deleteTestCases")
    void testDeleteRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, false);
    }

    // Java7 tests
    @ParameterizedTest
    @MethodSource("selectSingleTestCases")
    void testSelectSingleJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("selectOptionalTestCases")
    void testSelectOptionalJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("selectListTestCases")
    void testSelectListJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("cursorTestCases")
    void testCursorJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    void testInsertJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("updateTestCases")
    void testUpdateJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, true, false);
    }

    @ParameterizedTest
    @MethodSource("deleteTestCases")
    void testDeleteJava7Regression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, true, false);
    }

    // Spring tests (Java8 + Spring)
    @ParameterizedTest
    @MethodSource("selectSingleTestCases")
    void testSelectSingleSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("selectOptionalTestCases")
    void testSelectOptionalSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("selectListTestCases")
    void testSelectListSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("cursorTestCases")
    void testCursorSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DQL_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("insertTestCases")
    void testInsertSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("updateTestCases")
    void testUpdateSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, true);
    }

    @ParameterizedTest
    @MethodSource("deleteTestCases")
    void testDeleteSpringRegression(String testCase) throws Exception {
        runRegressionTest(testCase, DML_DIR, false, true);
    }

    private void runRegressionTest(String testCase, String baseDir, boolean java7, boolean spring) {
        String yamlFile = testCase + ".yaml";
        String variant = (java7 ? "-java7" : "") + (spring ? "-spring" : "");
        String snapshotName = testCase.replace("/", "-") + variant;
        
        TestResourceManager.withCleanup(manager -> {
            String testPrefix = testCase.contains("insert") ? "dml-insert" :
                               testCase.contains("update") ? "dml-update" :
                               testCase.contains("delete") ? "dml-delete" : "dql-regression";
            File testDir = manager.createTempDirectory(testPrefix + "-" + snapshotName);
            
            File sourceYaml = new File(baseDir + "/" + yamlFile);
            if (!sourceYaml.exists()) {
                sourceYaml = new File("src/test/resources/" + yamlFile);
            }
            assertTrue(sourceYaml.exists(), "YAML file should exist: " + yamlFile);
            
            File configFile = new File(testDir, "dao.yaml");
            java.nio.file.Files.copy(sourceYaml.toPath(), configFile.toPath());
            
            File generatedDir = new File(testDir, "generated");
            generatedDir.mkdirs();

            SqlMojo mojo = new SqlMojo();
            org.apache.maven.plugin.descriptor.PluginDescriptor pluginDescriptor = new org.apache.maven.plugin.descriptor.PluginDescriptor();
            pluginDescriptor.setGroupId("io.github.epi155");
            pluginDescriptor.setArtifactId("emsql-maven-plugin");
            pluginDescriptor.setVersion("1.1-A3-SNAPSHOT");
            mojo.setPlugin(pluginDescriptor);
            
            mojo.setConfigDirectory(testDir);
            mojo.setGenerateDirectory(generatedDir);
            mojo.setModules(new String[]{"dao.yaml"});
            mojo.setDebugCode(true);
            mojo.setAddCompileSourceRoot(false);
            mojo.setAddTestCompileSourceRoot(false);
            mojo.setJava7(java7);
            mojo.setProvider(spring ? "Spring" : null);

            assertDoesNotThrow(() -> mojo.execute(), "Plugin should execute without errors");

            List<File> generatedFiles = GeneratedCodeValidator.getAllGeneratedFiles(generatedDir);
            assertFalse(generatedFiles.isEmpty(), "Should generate at least one file");
            
            File generatedFile = generatedFiles.get(0);
            String content = java.nio.file.Files.readString(generatedFile.toPath());
            
            assertTrue(content.contains("import io.github.epi155.emsql.runtime."), 
                "Generated code should import runtime package");
            
            if (spring) {
                assertTrue(content.contains("@Repository") || content.contains("qualifier"), 
                    "Spring variant should contain Spring annotations");
            }
            
            SnapshotManager.SnapshotComparison comparison = snapshotManager.compareWithSnapshot(
                generatedFile, snapshotName);
            
            if (comparison.isSuccess()) {
                log.info("Snapshot passed for {}", snapshotName);
            } else {
                if (comparison.getMessage().contains("not found")) {
                    snapshotManager.captureSnapshot(generatedFile, snapshotName);
                    log.info("Created snapshot for {}", snapshotName);
                } else {
                    log.warn("Snapshot differs for {}: {}", snapshotName, comparison.getMessage());
                }
            }

            CompilationTester.CompilationResult result = compilationTester.compileFile(generatedFile);
            assertTrue(result.isSuccess(), "Compilation should succeed: " + result.getMessage());
        });
    }

    @Test
    void testAllRegressionCasesExist() {
        List<String> missing = new ArrayList<>();
        
        Stream.of(
            selectSingleTestCases(),
            selectOptionalTestCases(),
            selectListTestCases(),
            cursorTestCases(),
            insertTestCases(),
            updateTestCases(),
            deleteTestCases()
        ).flatMap(s -> s).forEach(tc -> {
            String dir = tc.contains("insert") || tc.contains("update") || tc.contains("delete") ? DML_DIR : DQL_DIR;
            if (!new File(dir + "/" + tc + ".yaml").exists()) {
                missing.add(tc);
            }
        });
        
        assertTrue(missing.isEmpty(), "All test cases should exist: " + missing);
    }
}
