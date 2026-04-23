package io.github.epi155.emsql.test.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility class for validating generated Java code
 */
@Slf4j
public class GeneratedCodeValidator {

    private static final Pattern CLASS_PATTERN = Pattern.compile("public\\s+class\\s+(\\w+)");
    private static final Pattern METHOD_PATTERN = Pattern.compile("public\\s+static\\s+<[^>]+>\\s+(\\w+)\\s*\\(");
    private static final Pattern INTERFACE_PATTERN = Pattern.compile("public\\s+interface\\s+(\\w+)");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([^;]+);");

    /**
     * Validates that a generated Java file exists and has basic structure
     */
    public static void validateGeneratedFile(File javaFile) {
        assertTrue(javaFile.exists(), "Generated file should exist: " + javaFile.getPath());
        assertTrue(javaFile.isFile(), "Generated path should be a file: " + javaFile.getPath());
        assertTrue(javaFile.length() > 0, "Generated file should not be empty: " + javaFile.getPath());
    }

    /**
     * Validates the package structure of generated code
     */
    public static void validatePackageStructure(File rootDir, String expectedPackage) {
        String packagePath = expectedPackage.replace('.', File.separatorChar);
        File packageDir = new File(rootDir, packagePath);
        
        assertTrue(packageDir.exists(), "Package directory should exist: " + packageDir.getPath());
        assertTrue(packageDir.isDirectory(), "Package path should be a directory: " + packageDir.getPath());
    }

    /**
     * Validates that generated code contains expected class
     */
    public static void validateClassStructure(File javaFile, String expectedClassName) {
        String content = readFileContent(javaFile);
        
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(content);
        assertTrue(packageMatcher.find(), "Should have package declaration");
        
        Matcher classMatcher = CLASS_PATTERN.matcher(content);
        assertTrue(classMatcher.find(), "Should have class declaration");
        assertEquals(expectedClassName, classMatcher.group(1), "Class name should match expected");
        
        // Check for copyright header
        assertTrue(content.contains("Code Generated at"), "Should have generation timestamp");
        assertTrue(content.contains("Plugin:"), "Should have plugin information");
    }

    /**
     * Validates that generated code contains expected methods
     */
    public static void validateMethodSignatures(File javaFile, List<String> expectedMethods) {
        String content = readFileContent(javaFile);
        Matcher methodMatcher = METHOD_PATTERN.matcher(content);
        
        List<String> actualMethods = new ArrayList<>();
        while (methodMatcher.find()) {
            actualMethods.add(methodMatcher.group(1));
        }
        
        for (String expectedMethod : expectedMethods) {
            assertTrue(actualMethods.contains(expectedMethod), 
                "Expected method '" + expectedMethod + "' not found in generated code. Found methods: " + actualMethods);
        }
        
        log.info("Validated {} methods in {}", actualMethods.size(), javaFile.getName());
    }

    /**
     * Validates that generated code contains expected interfaces
     */
    public static void validateInterfaceStructure(File javaFile, List<String> expectedInterfaces) {
        String content = readFileContent(javaFile);
        Matcher interfaceMatcher = INTERFACE_PATTERN.matcher(content);
        
        List<String> actualInterfaces = new ArrayList<>();
        while (interfaceMatcher.find()) {
            actualInterfaces.add(interfaceMatcher.group(1));
        }
        
        for (String expectedInterface : expectedInterfaces) {
            assertTrue(actualInterfaces.contains(expectedInterface),
                "Expected interface '" + expectedInterface + "' not found in generated code");
        }
        
        log.info("Validated {} interfaces in {}", actualInterfaces.size(), javaFile.getName());
    }

    /**
     * Validates imports in generated code
     */
    public static void validateImports(File javaFile, List<String> expectedImports) {
        String content = readFileContent(javaFile);
        
        for (String expectedImport : expectedImports) {
            assertTrue(content.contains("import " + expectedImport + ";"),
                "Expected import '" + expectedImport + "' not found in generated code");
        }
    }

    /**
     * Validates that generated code compiles (basic syntax check)
     */
    public static void validateBasicSyntax(File javaFile) {
        String content = readFileContent(javaFile);
        
        // Basic syntax checks
        assertTrue(content.contains("public class"), "Should contain class declaration");
        assertTrue(content.contains("package"), "Should contain package declaration");
        
        // Check for balanced braces
        long openBraces = content.chars().filter(ch -> ch == '{').count();
        long closeBraces = content.chars().filter(ch -> ch == '}').count();
        assertEquals(openBraces, closeBraces, "Braces should be balanced");
        
        // Check for balanced parentheses
        long openParens = content.chars().filter(ch -> ch == '(').count();
        long closeParens = content.chars().filter(ch -> ch == ')').count();
        assertEquals(openParens, closeParens, "Parentheses should be balanced");
    }

    /**
     * Counts the number of classes generated in a directory
     */
    public static int countGeneratedClasses(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return 0;
        }
        
        return (int) directory.listFiles(file -> 
            file.isFile() && file.getName().endsWith(".java")
        ).length;
    }

    /**
     * Gets all generated Java files from a directory recursively
     */
    public static List<File> getAllGeneratedFiles(File rootDir) {
        List<File> javaFiles = new ArrayList<>();
        collectJavaFiles(rootDir, javaFiles);
        return javaFiles;
    }

    private static void collectJavaFiles(File dir, List<File> javaFiles) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    collectJavaFiles(file, javaFiles);
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
    }

    /**
     * Validates that generated code contains expected SQL patterns
     */
    public static void validateSqlPatterns(File javaFile, List<String> expectedSqlPatterns) {
        String content = readFileContent(javaFile);
        
        for (String sqlPattern : expectedSqlPatterns) {
            assertTrue(content.contains(sqlPattern),
                "Expected SQL pattern '" + sqlPattern + "' not found in generated code");
        }
    }

    /**
     * Validates Spring-specific annotations if present
     */
    public static void validateSpringAnnotations(File javaFile, List<String> expectedAnnotations) {
        String content = readFileContent(javaFile);
        
        for (String annotation : expectedAnnotations) {
            assertTrue(content.contains("@" + annotation),
                "Expected Spring annotation '@" + annotation + "' not found in generated code");
        }
    }

    /**
     * Reads file content safely
     */
    private static String readFileContent(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            fail("Could not read file: " + file.getPath() + " - " + e.getMessage());
            return null; // unreachable
        }
    }

    /**
     * Validates generated code matches expected structure for DAO classes
     */
    public static void validateDaoStructure(File javaFile, String className, List<String> methods) {
        validateGeneratedFile(javaFile);
        validateClassStructure(javaFile, className);
        validateMethodSignatures(javaFile, methods);
        validateBasicSyntax(javaFile);
        
        log.info("Successfully validated DAO structure for class: {}", className);
    }

    /**
     * Validates that generated code has proper error handling
     */
    public static void validateErrorHandling(File javaFile) {
        String content = readFileContent(javaFile);
        
        // Should contain SQLException handling
        boolean z = content.contains("throws SQLException") || content.contains("catch (SQLException");
        if (!z) {
            log.warn("Generated code does not seem to handle SQLException properly in {}", javaFile.getName());
            log.warn("Generated code does not seem to handle SQLException properly in {}", javaFile.getName());
            log.warn("Generated code does not seem to handle SQLException properly in {}", javaFile.getName());
            throw new RuntimeException("Generated code should handle SQLException");
        }
        assertTrue(content.contains("throws SQLException") || content.contains("catch (SQLException"),
            "Generated code should handle SQLException");
        
        log.info("Validated error handling in {}", javaFile.getName());
    }
}
