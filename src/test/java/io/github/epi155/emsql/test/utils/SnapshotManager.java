package io.github.epi155.emsql.test.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility class for managing snapshot testing and regression detection
 */
@Slf4j
public class SnapshotManager {

    private final Path snapshotDirectory;
    private final boolean updateSnapshots;

    public SnapshotManager() {
        this("src/test/resources/snapshots", false);
    }

    public SnapshotManager(String snapshotPath, boolean updateSnapshots) {
        this.snapshotDirectory = Paths.get(snapshotPath);
        this.updateSnapshots = updateSnapshots;
        
        try {
            Files.createDirectories(snapshotDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create snapshot directory: " + snapshotPath, e);
        }
    }

    /**
     * Captures a snapshot of a generated file
     */
    public void captureSnapshot(File generatedFile, String snapshotName) {
        String relativePath = getRelativePath(generatedFile);
        Path snapshotFile = snapshotDirectory.resolve(snapshotName + "-" + generatedFile.getName() + ".snapshot");
        
        try {
            String content = Files.readString(generatedFile.toPath());
            Files.writeString(snapshotFile, content);
            log.info("Captured snapshot for {} at {}", relativePath, snapshotFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture snapshot for " + generatedFile.getPath(), e);
        }
    }

    /**
     * Compares generated file with existing snapshot
     */
    public SnapshotComparison compareWithSnapshot(File generatedFile, String snapshotName) {
        Path snapshotFile = snapshotDirectory.resolve(snapshotName + "-" + generatedFile.getName() + ".snapshot");
        
        if (!Files.exists(snapshotFile)) {
            if (updateSnapshots) {
                captureSnapshot(generatedFile, snapshotName);
                return SnapshotComparison.created("Snapshot created as it didn't exist");
            } else {
                return SnapshotComparison.failure("Snapshot not found: " + snapshotFile);
            }
        }

        try {
            String currentContent = Files.readString(generatedFile.toPath());
            String snapshotContent = Files.readString(snapshotFile);
            
            if (currentContent.equals(snapshotContent)) {
                return SnapshotComparison.success("Content matches snapshot");
            } else {
                if (updateSnapshots) {
                    captureSnapshot(generatedFile, snapshotName);
                    return SnapshotComparison.updated("Content differs, snapshot updated");
                } else {
                    return SnapshotComparison.failure("Content differs from snapshot", 
                        createDiff(snapshotContent, currentContent));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compare snapshot for " + generatedFile.getPath(), e);
        }
    }

    /**
     * Compares entire directory structure with snapshots
     */
    public DirectoryComparison compareDirectoryWithSnapshot(File generatedDir, String snapshotName) {
        List<File> generatedFiles = GeneratedCodeValidator.getAllGeneratedFiles(generatedDir);
        List<SnapshotComparison> comparisons = new ArrayList<>();
        
        for (File generatedFile : generatedFiles) {
            String relativePath = getRelativePath(generatedFile);
            String fileSnapshotName = snapshotName + "-" + relativePath.replace(File.separatorChar, '-');
            
            SnapshotComparison comparison = compareWithSnapshot(generatedFile, fileSnapshotName);
            comparisons.add(comparison);
        }
        
        long failures = comparisons.stream().filter(c -> !c.isSuccess()).count();
        
        if (failures == 0) {
            return DirectoryComparison.success("All files match snapshots", comparisons);
        } else {
            return DirectoryComparison.failure(failures + " files differ from snapshots", comparisons);
        }
    }

    /**
     * Creates hash of file content for quick comparison
     */
    public String createContentHash(File file) {
        try {
            String content = Files.readString(file.toPath());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes());
            return bytesToHex(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create hash for " + file.getPath(), e);
        }
    }

    /**
     * Validates that all expected snapshots exist
     */
    public void validateSnapshotCompleteness(List<String> expectedSnapshots) {
        List<String> missingSnapshots = new ArrayList<>();
        
        for (String expectedSnapshot : expectedSnapshots) {
            Path snapshotFile = snapshotDirectory.resolve(expectedSnapshot + ".snapshot");
            if (!Files.exists(snapshotFile)) {
                missingSnapshots.add(expectedSnapshot);
            }
        }
        
        if (!missingSnapshots.isEmpty()) {
            fail("Missing expected snapshots: " + String.join(", ", missingSnapshots));
        }
    }

    /**
     * Lists all available snapshots
     */
    public List<String> listAvailableSnapshots() {
        try {
            return Files.list(snapshotDirectory)
                .filter(path -> path.toString().endsWith(".snapshot"))
                .map(path -> path.getFileName().toString().replace(".snapshot", ""))
                .sorted()
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list snapshots", e);
        }
    }

    /**
     * Removes specific snapshot
     */
    public void removeSnapshot(String snapshotName) {
        Path snapshotFile = snapshotDirectory.resolve(snapshotName + ".snapshot");
        try {
            Files.deleteIfExists(snapshotFile);
            log.info("Removed snapshot: {}", snapshotName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to remove snapshot: " + snapshotName, e);
        }
    }

    /**
     * Clears all snapshots
     */
    public void clearAllSnapshots() {
        try {
            Files.list(snapshotDirectory)
                .filter(path -> path.toString().endsWith(".snapshot"))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("Failed to delete snapshot: {}", path);
                    }
                });
            log.info("Cleared all snapshots");
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear snapshots", e);
        }
    }

    /**
     * Creates baseline snapshots for regression testing
     */
    public void createBaseline(File generatedDir, String baselineName) {
        List<File> generatedFiles = GeneratedCodeValidator.getAllGeneratedFiles(generatedDir);
        
        for (File generatedFile : generatedFiles) {
            String relativePath = getRelativePath(generatedFile);
            String snapshotName = baselineName + "-" + relativePath.replace(File.separatorChar, '-');
            captureSnapshot(generatedFile, snapshotName);
        }
        
        log.info("Created baseline '{}' with {} files", baselineName, generatedFiles.size());
    }

    /**
     * Validates against baseline
     */
    public void validateAgainstBaseline(File generatedDir, String baselineName) {
        DirectoryComparison comparison = compareDirectoryWithSnapshot(generatedDir, baselineName);
        
        if (!comparison.isSuccess()) {
            String message = "Regression detected: " + comparison.getMessage();
            if (!comparison.getDifferences().isEmpty()) {
                message += "\nDifferences:\n" + comparison.getDifferences().stream()
                    .filter(c -> !c.isSuccess())
                    .map(c -> "  - " + c.getMessage())
                    .collect(Collectors.joining("\n"));
            }
            fail(message);
        }
        
        log.info("Baseline validation passed for '{}'", baselineName);
    }

    private String getRelativePath(File file) {
        try {
            Path basePath = Paths.get("").toAbsolutePath();
            Path filePath = file.toPath().toAbsolutePath();
            return basePath.relativize(filePath).toString();
        } catch (Exception e) {
            return file.getPath();
        }
    }

    private String createDiff(String expected, String actual) {
        String[] expectedLines = expected.split("\n");
        String[] actualLines = actual.split("\n");
        
        List<String> diff = new ArrayList<>();
        int maxLines = Math.max(expectedLines.length, actualLines.length);
        
        for (int i = 0; i < maxLines; i++) {
            String expectedLine = i < expectedLines.length ? expectedLines[i] : "";
            String actualLine = i < actualLines.length ? actualLines[i] : "";
            
            if (!expectedLine.equals(actualLine)) {
                diff.add(String.format("Line %d: Expected '%s' but was '%s'", i + 1, expectedLine, actualLine));
            }
        }
        
        return String.join("\n", diff);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Result of snapshot comparison
     */
    public static class SnapshotComparison {
        private final boolean success;
        private final boolean updated;
        private final String message;
        private final String diff;

        private SnapshotComparison(boolean success, boolean updated, String message, String diff) {
            this.success = success;
            this.updated = updated;
            this.message = message;
            this.diff = diff != null ? diff : "";
        }

        public static SnapshotComparison success(String message) {
            return new SnapshotComparison(true, false, message, null);
        }

        public static SnapshotComparison created(String message) {
            return new SnapshotComparison(true, true, message, null);
        }

        public static SnapshotComparison updated(String message) {
            return new SnapshotComparison(true, true, message, null);
        }

        public static SnapshotComparison failure(String message) {
            return new SnapshotComparison(false, false, message, null);
        }

        public static SnapshotComparison failure(String message, String diff) {
            return new SnapshotComparison(false, false, message, diff);
        }

        public boolean isSuccess() { return success; }
        public boolean isUpdated() { return updated; }
        public String getMessage() { return message; }
        public String getDiff() { return diff; }

        public void assertSuccess() {
            assertTrue(success, "Snapshot comparison should succeed: " + message);
        }

        public void assertUnchanged() {
            assertTrue(success && !updated, "Snapshot should be unchanged: " + message);
        }
    }

    /**
     * Result of directory comparison
     */
    public static class DirectoryComparison {
        private final boolean success;
        private final String message;
        private final List<SnapshotComparison> comparisons;

        private DirectoryComparison(boolean success, String message, List<SnapshotComparison> comparisons) {
            this.success = success;
            this.message = message;
            this.comparisons = comparisons != null ? comparisons : new ArrayList<>();
        }

        public static DirectoryComparison success(String message, List<SnapshotComparison> comparisons) {
            return new DirectoryComparison(true, message, comparisons);
        }

        public static DirectoryComparison failure(String message, List<SnapshotComparison> comparisons) {
            return new DirectoryComparison(false, message, comparisons);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<SnapshotComparison> getComparisons() { return comparisons; }
        public List<SnapshotComparison> getDifferences() { 
            return comparisons.stream().filter(c -> !c.isSuccess()).collect(Collectors.toList());
        }

        public void assertSuccess() {
            assertTrue(success, "Directory comparison should succeed: " + message);
        }
    }
}
