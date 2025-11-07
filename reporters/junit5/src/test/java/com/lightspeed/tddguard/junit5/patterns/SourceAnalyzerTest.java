package com.lightspeed.tddguard.junit5.patterns;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SourceAnalyzer.
 * Validates test file discovery.
 */
class SourceAnalyzerTest {

    @Test
    void shouldFindJavaTestFiles(@TempDir Path projectRoot) throws IOException {
        // Given
        Path testDir = projectRoot.resolve("src/test/java/com/example");
        Files.createDirectories(testDir);

        Path testFile1 = testDir.resolve("FooTest.java");
        Path testFile2 = testDir.resolve("BarTest.java");
        Files.writeString(testFile1, "public class FooTest {}");
        Files.writeString(testFile2, "public class BarTest {}");

        // Create a non-test file
        Path mainDir = projectRoot.resolve("src/main/java/com/example");
        Files.createDirectories(mainDir);
        Files.writeString(mainDir.resolve("Foo.java"), "public class Foo {}");

        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertEquals(2, testFiles.size());
        assertTrue(testFiles.contains(testFile1));
        assertTrue(testFiles.contains(testFile2));
    }

    @Test
    void shouldExcludeMainSourceFiles(@TempDir Path projectRoot) throws IOException {
        // Given
        Path mainDir = projectRoot.resolve("src/main/java");
        Files.createDirectories(mainDir);
        Files.writeString(mainDir.resolve("Main.java"), "public class Main {}");

        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertTrue(testFiles.isEmpty());
    }

    @Test
    void shouldHandleNestedTestDirectories(@TempDir Path projectRoot) throws IOException {
        // Given
        Path nestedTest = projectRoot.resolve("src/test/java/com/example/unit/api");
        Files.createDirectories(nestedTest);
        Path testFile = nestedTest.resolve("ApiTest.java");
        Files.writeString(testFile, "public class ApiTest {}");

        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertEquals(1, testFiles.size());
        assertEquals(testFile, testFiles.get(0));
    }

    @Test
    void shouldHandleEmptyProject(@TempDir Path projectRoot) {
        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertTrue(testFiles.isEmpty());
    }

    @Test
    void shouldFindKotlinTestFiles(@TempDir Path projectRoot) throws IOException {
        // Given
        Path kotlinTestDir = projectRoot.resolve("src/test/kotlin/com/example");
        Files.createDirectories(kotlinTestDir);
        Path kotlinTest = kotlinTestDir.resolve("TestSpec.kt");
        Files.writeString(kotlinTest, "class TestSpec");

        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertEquals(1, testFiles.size());
        assertTrue(testFiles.get(0).toString().endsWith(".kt"));
    }

    @Test
    void shouldExcludeBuildDirectories(@TempDir Path projectRoot) throws IOException {
        // Given
        Path buildTest = projectRoot.resolve("build/test-classes/com/example");
        Files.createDirectories(buildTest);
        Files.writeString(buildTest.resolve("Test.java"), "public class Test {}");

        Path realTest = projectRoot.resolve("src/test/java/com/example");
        Files.createDirectories(realTest);
        Path realTestFile = realTest.resolve("RealTest.java");
        Files.writeString(realTestFile, "public class RealTest {}");

        // When
        List<Path> testFiles = SourceAnalyzer.findTestFiles(projectRoot);

        // Then
        assertEquals(1, testFiles.size());
        assertEquals(realTestFile, testFiles.get(0));
    }
}
