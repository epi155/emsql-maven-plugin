package io.github.epi155.emsql.test.utils;

import lombok.extern.slf4j.Slf4j;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility class for testing compilation of generated Java code
 */
@Slf4j
public class CompilationTester {

    private final JavaCompiler compiler;
    private final List<String> classpath;
    private final List<String> options;

    public CompilationTester() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java compiler not available. Are you running with JRE instead of JDK?");
        }
        
        this.classpath = determineClasspath();
        this.options = Arrays.asList("-classpath", String.join(File.pathSeparator, classpath));
        
        log.debug("Initialized CompilationTester with classpath: {}", classpath);
    }

    /**
     * Compiles all Java files in a directory
     */
    public CompilationResult compileDirectory(File sourceDir) {
        return compileDirectory(sourceDir, null);
    }

    /**
     * Compiles all Java files in a directory to a specific output directory
     */
    public CompilationResult compileDirectory(File sourceDir, File outputDir) {
        List<File> javaFiles = findJavaFiles(sourceDir);
        
        if (javaFiles.isEmpty()) {
            return CompilationResult.success("No Java files found to compile");
        }

        log.info("Compiling {} Java files from directory: {}", javaFiles.size(), sourceDir.getPath());

        try {
            List<String> compileOptions = new ArrayList<>(options);
            if (outputDir != null) {
                outputDir.mkdirs();
                compileOptions.add("-d");
                compileOptions.add(outputDir.getPath());
            }

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(javaFiles);

            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, null, compileOptions, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (success) {
                log.info("Compilation successful for {} files", javaFiles.size());
                return CompilationResult.success("Successfully compiled " + javaFiles.size() + " files");
            } else {
                String errorMsg = "Compilation failed for " + javaFiles.size() + " files";
                log.error(errorMsg);
                return CompilationResult.failure(errorMsg);
            }

        } catch (IOException e) {
            String errorMsg = "IO error during compilation: " + e.getMessage();
            log.error(errorMsg, e);
            return CompilationResult.failure(errorMsg);
        }
    }

    /**
     * Compiles a single Java file
     */
    public CompilationResult compileFile(File javaFile) {
        return compileFile(javaFile, null);
    }

    /**
     * Compiles a single Java file to a specific output directory
     */
    public CompilationResult compileFile(File javaFile, File outputDir) {
        if (!javaFile.exists() || !javaFile.getName().endsWith(".java")) {
            return CompilationResult.failure("Invalid Java file: " + javaFile.getPath());
        }

        log.info("Compiling single file: {}", javaFile.getPath());

        try {
            List<String> compileOptions = new ArrayList<>(options);
            if (outputDir != null) {
                outputDir.mkdirs();
                compileOptions.add("-d");
                compileOptions.add(outputDir.getPath());
            }

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));

            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, null, compileOptions, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (success) {
                log.info("Compilation successful for file: {}", javaFile.getName());
                return CompilationResult.success("Successfully compiled " + javaFile.getName());
            } else {
                String errorMsg = "Compilation failed for file: " + javaFile.getName();
                log.error(errorMsg);
                return CompilationResult.failure(errorMsg);
            }

        } catch (IOException e) {
            String errorMsg = "IO error during compilation: " + e.getMessage();
            log.error(errorMsg, e);
            return CompilationResult.failure(errorMsg);
        }
    }

    /**
     * Compiles with detailed diagnostic information
     */
    public CompilationResult compileWithDiagnostics(File sourceDir) {
        List<File> javaFiles = findJavaFiles(sourceDir);
        
        if (javaFiles.isEmpty()) {
            return CompilationResult.success("No Java files found to compile");
        }

        log.info("Compiling with diagnostics: {} files", javaFiles.size());

        try {
            List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();
            DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null);
            
            Iterable<? extends JavaFileObject> compilationUnits = 
                fileManager.getJavaFileObjectsFromFiles(javaFiles);

            JavaCompiler.CompilationTask task = compiler.getTask(
                null, fileManager, diagnosticCollector, options, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            List<String> diagnosticMessages = diagnostics.stream()
                .map(d -> formatDiagnostic(d))
                .collect(Collectors.toList());

            if (success) {
                log.info("Compilation successful with {} diagnostics", diagnostics.size());
                return CompilationResult.success("Compilation successful", diagnosticMessages);
            } else {
                log.error("Compilation failed with {} diagnostics", diagnostics.size());
                return CompilationResult.failure("Compilation failed", diagnosticMessages);
            }

        } catch (IOException e) {
            String errorMsg = "IO error during compilation: " + e.getMessage();
            log.error(errorMsg, e);
            return CompilationResult.failure(errorMsg);
        }
    }

    /**
     * Tests if generated classes can be loaded (basic runtime test)
     */
    public RuntimeTestResult testGeneratedClasses(File compiledClassesDir, String... classNames) {
        if (!compiledClassesDir.exists()) {
            return RuntimeTestResult.failure("Compiled classes directory does not exist");
        }

        log.info("Testing runtime loading of {} classes", classNames.length);

        try {
            // Create a simple class loader for the compiled classes
            TestClassLoader classLoader = new TestClassLoader(compiledClassesDir.toPath());
            
            List<String> loadedClasses = new ArrayList<>();
            List<String> failedClasses = new ArrayList<>();

            for (String className : classNames) {
                try {
                    classLoader.loadClass(className);
                    loadedClasses.add(className);
                    log.debug("Successfully loaded class: {}", className);
                } catch (ClassNotFoundException e) {
                    failedClasses.add(className + ": " + e.getMessage());
                    log.warn("Failed to load class: {} - {}", className, e.getMessage());
                }
            }

            if (failedClasses.isEmpty()) {
                return RuntimeTestResult.success("All classes loaded successfully", loadedClasses);
            } else {
                return RuntimeTestResult.failure("Some classes failed to load", loadedClasses, failedClasses);
            }

        } catch (Exception e) {
            return RuntimeTestResult.failure("Error during runtime test: " + e.getMessage());
        }
    }

    /**
     * Validates that generated code has proper dependencies
     */
    public boolean validateDependencies(File sourceDir) {
        List<File> javaFiles = findJavaFiles(sourceDir);
        
        for (File javaFile : javaFiles) {
            try {
                String content = Files.readString(javaFile.toPath());
                
                // Check for required imports
                if (content.contains("SQLException") && !content.contains("java.sql.SQLException")) {
                    log.warn("Missing java.sql.SQLException import in {}", javaFile.getName());
                    return false;
                }
                
                if (content.contains("Connection") && !content.contains("java.sql.Connection")) {
                    log.warn("Missing java.sql.Connection import in {}", javaFile.getName());
                    return false;
                }

            } catch (IOException e) {
                log.error("Error reading file {}: {}", javaFile.getPath(), e.getMessage());
                return false;
            }
        }
        
        return true;
    }

    private List<File> findJavaFiles(File dir) {
        List<File> javaFiles = new ArrayList<>();
        if (dir.isDirectory()) {
            findJavaFilesRecursive(dir, javaFiles);
        }
        return javaFiles;
    }

    private void findJavaFilesRecursive(File dir, List<File> javaFiles) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findJavaFilesRecursive(file, javaFiles);
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
    }

    private List<String> determineClasspath() {
        List<String> cp = new ArrayList<>();
        
        // Add current classpath
        String classpath = System.getProperty("java.class.path");
        if (classpath != null && !classpath.isEmpty()) {
            cp.addAll(Arrays.asList(classpath.split(File.pathSeparator)));
        }
        
        // Add common test dependencies
        cp.add(System.getProperty("java.home") + "/lib/rt.jar");
        
        return cp.stream()
            .filter(path -> path != null && !path.isEmpty())
            .distinct()
            .collect(Collectors.toList());
    }

    private String formatDiagnostic(Diagnostic<?> diagnostic) {
        return String.format("Line %d: %s - %s", 
            diagnostic.getLineNumber(), 
            diagnostic.getKind(), 
            diagnostic.getMessage(null));
    }

    /**
     * Result of compilation operation
     */
    public static class CompilationResult {
        private final boolean success;
        private final String message;
        private final List<String> diagnostics;

        private CompilationResult(boolean success, String message, List<String> diagnostics) {
            this.success = success;
            this.message = message;
            this.diagnostics = diagnostics != null ? diagnostics : new ArrayList<>();
        }

        public static CompilationResult success(String message) {
            return new CompilationResult(true, message, null);
        }

        public static CompilationResult success(String message, List<String> diagnostics) {
            return new CompilationResult(true, message, diagnostics);
        }

        public static CompilationResult failure(String message) {
            return new CompilationResult(false, message, null);
        }

        public static CompilationResult failure(String message, List<String> diagnostics) {
            return new CompilationResult(false, message, diagnostics);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<String> getDiagnostics() { return diagnostics; }

        public void assertSuccess() {
            assertTrue(success, "Compilation should succeed: " + message);
        }

        public void assertFailure() {
            assertFalse(success, "Compilation should fail: " + message);
        }
    }

    /**
     * Result of runtime testing operation
     */
    public static class RuntimeTestResult {
        private final boolean success;
        private final String message;
        private final List<String> loadedClasses;
        private final List<String> failedClasses;

        private RuntimeTestResult(boolean success, String message, List<String> loadedClasses, List<String> failedClasses) {
            this.success = success;
            this.message = message;
            this.loadedClasses = loadedClasses != null ? loadedClasses : new ArrayList<>();
            this.failedClasses = failedClasses != null ? failedClasses : new ArrayList<>();
        }

        public static RuntimeTestResult success(String message, List<String> loadedClasses) {
            return new RuntimeTestResult(true, message, loadedClasses, null);
        }

        public static RuntimeTestResult failure(String message) {
            return new RuntimeTestResult(false, message, null, null);
        }

        public static RuntimeTestResult failure(String message, List<String> loadedClasses, List<String> failedClasses) {
            return new RuntimeTestResult(false, message, loadedClasses, failedClasses);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<String> getLoadedClasses() { return loadedClasses; }
        public List<String> getFailedClasses() { return failedClasses; }

        public void assertSuccess() {
            assertTrue(success, "Runtime test should succeed: " + message);
        }

        public void assertFailure() {
            assertFalse(success, "Runtime test should fail: " + message);
        }
    }

    /**
     * Simple class loader for testing compiled classes
     */
    private static class TestClassLoader extends ClassLoader {
        private final Path classesDir;

        public TestClassLoader(Path classesDir) {
            super();
            this.classesDir = classesDir;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String path = name.replace('.', '/') + ".class";
            Path classFile = classesDir.resolve(path);

            if (Files.exists(classFile)) {
                try {
                    byte[] classData = Files.readAllBytes(classFile);
                    return defineClass(name, classData, 0, classData.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException("Failed to read class file: " + classFile, e);
                }
            }

            throw new ClassNotFoundException("Class not found: " + name);
        }
    }
}
