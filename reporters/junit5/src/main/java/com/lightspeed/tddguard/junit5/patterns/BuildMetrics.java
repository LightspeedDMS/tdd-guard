package com.lightspeed.tddguard.junit5.patterns;

import java.nio.file.Path;

/**
 * Build and compilation metrics for pattern detection.
 * Provides measurable data about build performance.
 */
public class BuildMetrics {

    private final long testCompilationTime;
    private final boolean incrementalCompilationEnabled;

    /**
     * Creates build metrics with explicit values.
     *
     * @param testCompilationTime           Test compilation time in milliseconds
     * @param incrementalCompilationEnabled Whether incremental compilation is enabled
     * @throws IllegalArgumentException if compilationTime is negative
     */
    public BuildMetrics(long testCompilationTime, boolean incrementalCompilationEnabled) {
        if (testCompilationTime < 0) {
            throw new IllegalArgumentException("Compilation time cannot be negative");
        }
        this.testCompilationTime = testCompilationTime;
        this.incrementalCompilationEnabled = incrementalCompilationEnabled;
    }

    /**
     * Returns empty metrics with default values.
     *
     * @return Empty build metrics
     */
    public static BuildMetrics empty() {
        return new BuildMetrics(0L, false);
    }

    /**
     * Collects build metrics from project.
     * Currently returns defaults - future enhancement will parse build output.
     *
     * @param projectRoot Project root directory
     * @return Build metrics (defaults for now)
     */
    public static BuildMetrics collect(Path projectRoot) {
        // Future enhancement: Parse Gradle build scan or Maven surefire reports
        // For now, return defaults
        return empty();
    }

    /**
     * Returns test compilation time in milliseconds.
     *
     * @return Compilation time in ms
     */
    public long getTestCompilationTime() {
        return testCompilationTime;
    }

    /**
     * Returns whether incremental compilation is enabled.
     *
     * @return true if incremental compilation enabled
     */
    public boolean isIncrementalCompilationEnabled() {
        return incrementalCompilationEnabled;
    }
}
