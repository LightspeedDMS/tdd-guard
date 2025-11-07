package com.lightspeed.tddguard.junit5.patterns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility for analyzing source code structure.
 * Finds test files and extracts patterns.
 */
public class SourceAnalyzer {

    /**
     * Finds all test source files in the project.
     * Includes Java and Kotlin test files from src/test directories.
     * Excludes build output directories.
     *
     * @param projectRoot Project root directory
     * @return List of test file paths
     */
    public static List<Path> findTestFiles(Path projectRoot) {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java") || p.toString().endsWith(".kt"))
                .filter(p -> p.toString().contains("/src/test/"))
                .filter(p -> !p.toString().contains("/build/"))
                .filter(p -> !p.toString().contains("/target/"))
                .collect(Collectors.toList());
        } catch (IOException e) {
            // Return empty list if walk fails
            return Collections.emptyList();
        }
    }
}
