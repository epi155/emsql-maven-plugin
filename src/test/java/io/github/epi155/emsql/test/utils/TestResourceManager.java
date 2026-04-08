package io.github.epi155.emsql.test.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for managing test resources and temporary directories
 */
@Slf4j
public class TestResourceManager {

    private final List<File> temporaryDirectories;
    private final List<File> temporaryFiles;
    private final String testId;

    public TestResourceManager() {
        this.temporaryDirectories = new ArrayList<>();
        this.temporaryFiles = new ArrayList<>();
        this.testId = UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Creates a temporary directory for test execution
     */
    public File createTempDirectory(String prefix) {
        try {
            Path tempDir = Files.createTempDirectory(prefix + "-" + testId);
            File dir = tempDir.toFile();
            temporaryDirectories.add(dir);
            log.debug("Created temporary directory: {}", dir.getPath());
            return dir;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory", e);
        }
    }

    /**
     * Creates a temporary file for test execution
     */
    public File createTempFile(String prefix, String suffix) {
        try {
            Path tempFile = Files.createTempFile(prefix + "-" + testId, suffix);
            File file = tempFile.toFile();
            temporaryFiles.add(file);
            log.debug("Created temporary file: {}", file.getPath());
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary file", e);
        }
    }

    /**
     * Gets a test resource file from classpath
     */
    public File getTestResource(String resourcePath) {
        File resource = new File("src/test/resources/" + resourcePath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Test resource not found: " + resourcePath);
        }
        return resource;
    }

    /**
     * Copies a test resource to a temporary location
     */
    public File copyTestResource(String resourcePath, File targetDir) {
        File source = getTestResource(resourcePath);
        File target = new File(targetDir, source.getName());
        
        try {
            Files.copy(source.toPath(), target.toPath());
            temporaryFiles.add(target);
            log.debug("Copied test resource {} to {}", resourcePath, target.getPath());
            return target;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy test resource: " + resourcePath, e);
        }
    }

    /**
     * Creates a test output directory structure
     */
    public File createTestOutputDirectory(String baseName) {
        File baseDir = createTempDirectory("emsql-test-" + baseName);
        File generatedDir = new File(baseDir, "generated-sources");
        File compiledDir = new File(baseDir, "compiled-classes");
        
        generatedDir.mkdirs();
        compiledDir.mkdirs();
        
        log.info("Created test output structure in {}", baseDir.getPath());
        return baseDir;
    }

    /**
     * Gets all YAML files from test resources
     */
    public List<File> getTestYamlFiles() {
        return getTestYamlFiles("src/test/resources");
    }

    /**
     * Gets all YAML files from a specific directory
     */
    public List<File> getTestYamlFiles(String directoryPath) {
        List<File> yamlFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (directory.exists() && directory.isDirectory()) {
            collectYamlFiles(directory, yamlFiles);
        }
        
        return yamlFiles;
    }

    /**
     * Gets specific YAML files for testing
     */
    public List<File> getSpecificYamlFiles(String... fileNames) {
        List<File> files = new ArrayList<>();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".yaml")) {
                files.add(getTestResource(fileName));
            } else {
                files.add(getTestResource(fileName + ".yaml"));
            }
        }
        return files;
    }

    /**
     * Creates a minimal test configuration
     */
    public File createMinimalTestConfig(File targetDir) {
        String yamlContent = "packageName: com.example.test\n" +
            "className: TestDao\n" +
            "declare:\n" +
            "  idInt: long\n" +
            "  int01: Int?\n" +
            "  int11: Int\n" +
            "methods:\n" +
            "  - methodName: insert01\n" +
            "    perform: !Insert\n" +
            "      execSql: |\n" +
            "        insert into tint (\n" +
            "          int01,\n" +
            "          int11\n" +
            "        ) values (\n" +
            "          :int01,\n" +
            "          :int11 \n" +
            "        )";
        
        File configFile = new File(targetDir, "test-config.yaml");
        try {
            Files.writeString(configFile.toPath(), yamlContent);
            temporaryFiles.add(configFile);
            log.debug("Created minimal test config: {}", configFile.getPath());
            return configFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test config", e);
        }
    }

    /**
     * Creates a test POM file for plugin testing
     */
    public File createTestPom(File targetDir, String pluginVersion, List<String> yamlModules) {
        String modulesXml = yamlModules.stream()
            .map(module -> "        <module>" + module + "</module>")
            .collect(java.util.stream.Collectors.joining("\n"));
        
        String pomTemplate = "<project>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>test</groupId>\n" +
            "    <artifactId>test-project</artifactId>\n" +
            "    <version>1.0.0</version>\n" +
            "    \n" +
            "    <build>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>io.github.epi155</groupId>\n" +
            "                <artifactId>emsql-maven-plugin</artifactId>\n" +
            "                <version>%s</version>\n" +
            "                <executions>\n" +
            "                    <execution>\n" +
            "                        <goals>\n" +
            "                            <goal>generate</goal>\n" +
            "                        </goals>\n" +
            "                    </execution>\n" +
            "                </executions>\n" +
            "                <configuration>\n" +
            "                    <configDirectory>${project.basedir}</configDirectory>\n" +
            "                    <generateDirectory>${project.basedir}/target/generated-sources</generateDirectory>\n" +
            "                    <debugCode>true</debugCode>\n" +
            "                    <modules>\n" +
            "        %s\n" +
            "                    </modules>\n" +
            "                    <addCompileSourceRoot>false</addCompileSourceRoot>\n" +
            "                    <addTestCompileSourceRoot>false</addTestCompileSourceRoot>\n" +
            "                </configuration>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>\n";
        
        String pomContent = String.format(pomTemplate, pluginVersion, modulesXml);
        
        File pomFile = new File(targetDir, "pom.xml");
        try {
            Files.writeString(pomFile.toPath(), pomContent);
            temporaryFiles.add(pomFile);
            log.debug("Created test POM: {}", pomFile.getPath());
            return pomFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test POM", e);
        }
    }

    /**
     * Creates a Spring-specific test configuration
     */
    public File createSpringTestConfig(File targetDir) {
        String yamlContent = "packageName: com.example.spring\n" +
            "className: SpringTestDao\n" +
            "qualifier: testRepository\n" +
            "declare:\n" +
            "  id: long\n" +
            "  name: VarChar\n" +
            "  created: LocalDate\n" +
            "methods:\n" +
            "  - methodName: findById\n" +
            "    perform: !SelectOptional\n" +
            "      execSql: |\n" +
            "        SELECT id, name, created\n" +
            "        FROM spring_test\n" +
            "        WHERE id = :id\n" +
            "  - methodName: save\n" +
            "    perform: !Insert\n" +
            "      execSql: |\n" +
            "        INSERT INTO spring_test (id, name, created)\n" +
            "        VALUES (:id, :name, :created)\n";
        
        File configFile = new File(targetDir, "spring-test-config.yaml");
        try {
            Files.writeString(configFile.toPath(), yamlContent);
            temporaryFiles.add(configFile);
            log.debug("Created Spring test config: {}", configFile.getPath());
            return configFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Spring test config", e);
        }
    }

    /**
     * Creates an invalid YAML configuration for error testing
     */
    public File createInvalidTestConfig(File targetDir) {
        String yamlContent = "packageName: com.example.invalid\n" +
            "className: InvalidDao\n" +
            "declare:\n" +
            "  id: unsupported_type\n" +
            "methods:\n" +
            "  - methodName: invalidMethod\n" +
            "    perform: !InvalidOperation\n" +
            "      execSql: |\n" +
            "        INVALID SQL SYNTAX HERE\n";
        
        File configFile = new File(targetDir, "invalid-config.yaml");
        try {
            Files.writeString(configFile.toPath(), yamlContent);
            temporaryFiles.add(configFile);
            log.debug("Created invalid test config: {}", configFile.getPath());
            return configFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create invalid test config", e);
        }
    }

    /**
     * Gets the test ID for this resource manager instance
     */
    public String getTestId() {
        return testId;
    }

    /**
     * Cleans up all temporary resources
     */
    public void cleanup() {
        cleanupFiles();
        cleanupDirectories();
        log.info("Cleaned up test resources for test ID: {}", testId);
    }

    private void cleanupFiles() {
        for (File file : temporaryFiles) {
            if (file.exists()) {
                if (!file.delete()) {
                    log.warn("Failed to delete temporary file: {}", file.getPath());
                }
            }
        }
        temporaryFiles.clear();
    }

    private void cleanupDirectories() {
        for (File dir : temporaryDirectories) {
            if (dir.exists()) {
                deleteDirectory(dir);
            }
        }
        temporaryDirectories.clear();
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        log.warn("Failed to delete file in directory: {}", file.getPath());
                    }
                }
            }
        }
        if (!directory.delete()) {
            log.warn("Failed to delete directory: {}", directory.getPath());
        }
    }

    private void collectYamlFiles(File directory, List<File> yamlFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectYamlFiles(file, yamlFiles);
                } else if (file.getName().endsWith(".yaml")) {
                    yamlFiles.add(file);
                }
            }
        }
    }

    /**
     * Utility method to run with automatic cleanup
     */
    public static void withCleanup(TestResourceManagerCallback callback) {
        TestResourceManager manager = new TestResourceManager();
        try {
            callback.execute(manager);
        } catch (Exception e) {
            throw new RuntimeException("Test execution failed", e);
        } finally {
            manager.cleanup();
        }
    }

    @FunctionalInterface
    public interface TestResourceManagerCallback {
        void execute(TestResourceManager manager) throws Exception;
    }
}
