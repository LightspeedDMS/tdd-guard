package com.lightspeed.tddguard.junit5;

import com.lightspeed.tddguard.junit5.model.TestCase;
import com.lightspeed.tddguard.junit5.model.TestFailure;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TestResultCollector.
 *
 * Covers AC#5: Test Lifecycle Capture
 */
class TestResultCollectorTest {

    @Test
    void shouldCollectPassedTest() {
        // Given: A test result collector
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // And: A passed test
        TestIdentifier testId = createTestIdentifier("testPassed", "com.example.MyTest", "testPassed()");
        collector.recordTestStart(testId);

        // Wait a bit to have duration
        try { Thread.sleep(10); } catch (InterruptedException e) {}

        TestExecutionResult result = TestExecutionResult.successful();

        // When: Recording the test result
        collector.recordTestFinish(testId, result);

        // Then: Should have one test with passed status
        List<TestCase> tests = collector.getTests();
        assertEquals(1, tests.size());

        TestCase test = tests.get(0);
        assertEquals("testPassed", test.name);
        assertEquals("passed", test.status);
        assertTrue(test.duration >= 0, "Duration should be >= 0");
        assertNotNull(test.displayName);
    }

    @Test
    void shouldCollectFailedTestWithError() {
        // Given: A test result collector
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // And: A failed test with exception
        TestIdentifier testId = createTestIdentifier("testFailed", "com.example.MyTest", "testFailed()");
        collector.recordTestStart(testId);

        AssertionError error = new AssertionError("Expected 5 but was 3");
        TestExecutionResult result = TestExecutionResult.failed(error);

        // When: Recording the test result
        collector.recordTestFinish(testId, result);

        // Then: Should have one test with failed status and error details
        List<TestCase> tests = collector.getTests();
        assertEquals(1, tests.size());
        TestCase test = tests.get(0);

        assertEquals("failed", test.status);

        List<TestFailure> failures = collector.getFailures();
        assertEquals(1, failures.size());
        assertEquals("Expected 5 but was 3", failures.get(0).message);
        assertNotNull(failures.get(0).stack);
        assertTrue(failures.get(0).stack.contains("AssertionError"));
    }

    @Test
    void shouldCollectSkippedTest() {
        // Given: A test result collector
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // And: A skipped test
        TestIdentifier testId = createTestIdentifier("testSkipped", "com.example.MyTest", "testSkipped()");

        // When: Recording as skipped
        collector.recordSkipped(testId, "Test disabled");

        // Then: Should have one test with skipped status
        List<TestCase> tests = collector.getTests();
        assertEquals(1, tests.size());
        assertEquals("skipped", tests.get(0).status);
    }

    @Test
    void shouldCollectMultipleTests() {
        // Given: A test result collector
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // And: Multiple tests from different classes
        TestIdentifier test1 = createTestIdentifier("test1", "com.example.FirstTest", "test1()");
        TestIdentifier test2 = createTestIdentifier("test2", "com.example.FirstTest", "test2()");
        TestIdentifier test3 = createTestIdentifier("test3", "com.example.SecondTest", "test3()");

        // When: Recording tests
        collector.recordTestStart(test1);
        collector.recordTestFinish(test1, TestExecutionResult.successful());

        collector.recordTestStart(test2);
        collector.recordTestFinish(test2, TestExecutionResult.successful());

        collector.recordTestStart(test3);
        collector.recordTestFinish(test3, TestExecutionResult.successful());

        // Then: Should have 3 tests
        List<TestCase> tests = collector.getTests();
        assertEquals(3, tests.size());
    }

    @Test
    void shouldCalculateSummaryCorrectly() {
        // Given: A test result collector with mixed results
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // When: Recording mixed test results
        TestIdentifier t1 = createTestIdentifier("t1", "Test", "t1()");
        collector.recordTestStart(t1);
        collector.recordTestFinish(t1, TestExecutionResult.successful());

        TestIdentifier t2 = createTestIdentifier("t2", "Test", "t2()");
        collector.recordTestStart(t2);
        collector.recordTestFinish(t2, TestExecutionResult.successful());

        TestIdentifier t3 = createTestIdentifier("t3", "Test", "t3()");
        collector.recordTestStart(t3);
        collector.recordTestFinish(t3, TestExecutionResult.failed(new Error("fail")));

        TestIdentifier t4 = createTestIdentifier("t4", "Test", "t4()");
        collector.recordSkipped(t4, "reason");

        // Then: Summary should be correct
        assertEquals(4, collector.getTotalTests());
        assertEquals(2, collector.getPassedTests());
        assertEquals(1, collector.getFailedTests());
        assertEquals(1, collector.getSkippedTests());
    }

    @Test
    void shouldTrackTestDuration() {
        // Given: A test result collector
        TestResultCollector collector = new TestResultCollector(Path.of("/project"));

        // And: A test that runs for measurable time
        TestIdentifier testId = createTestIdentifier("test", "Test", "test()");
        collector.recordTestStart(testId);

        // Wait 20ms
        try { Thread.sleep(20); } catch (InterruptedException e) {}

        collector.recordTestFinish(testId, TestExecutionResult.successful());

        // Then: Duration should be at least 15ms (allowing some variance)
        List<TestCase> tests = collector.getTests();
        assertTrue(tests.get(0).duration >= 15,
            "Expected duration >= 15ms, got " + tests.get(0).duration);
    }

    // Helper method to create test identifiers using real JUnit Platform APIs
    private TestIdentifier createTestIdentifier(String uniqueId, String className, String displayName) {
        MethodSource source = MethodSource.from(className, uniqueId);
        return TestIdentifier.from(
            new TestDescriptorStub(uniqueId, displayName, source)
        );
    }
}
