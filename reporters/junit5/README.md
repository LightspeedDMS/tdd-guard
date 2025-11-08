# TDD Guard JUnit5 Reporter

JUnit 5 Platform reporter that captures test execution data for TDD Guard enforcement.

## Features

- **Auto-discovery**: Automatically registered via JUnit Platform service provider mechanism
- **Self-detection**: Activates only when TDD Guard is present, zero overhead when disabled
- **Complete test capture**: All JUnit 5 test types (standard, @Nested, @ParameterizedTest, @RepeatedTest, @TestFactory)
- **JSON output**: Writes to `.claude/tdd-guard/data/test.json` in TDD Guard standard format
- **Error resilient**: Never fails tests due to reporter errors

## Usage

### Gradle

```kotlin
dependencies {
    testImplementation("com.lightspeed.tddguard:junit5:0.1.0")
}
```

### Maven

**Add dependency**:

```xml
<dependency>
    <groupId>com.lightspeed.tddguard</groupId>
    <artifactId>junit5</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
```

**Configure Surefire plugin** (required for system properties):

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <systemPropertyVariables>
                    <tddguard.projectRoot>${project.basedir}</tddguard.projectRoot>
                </systemPropertyVariables>
                <environmentVariables>
                    <TDDGUARD_ENABLED>true</TDDGUARD_ENABLED>
                </environmentVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**For GitHub Packages** (if not using mavenLocal):

```xml
<repositories>
    <repository>
        <id>github-tdd-guard</id>
        <url>https://maven.pkg.github.com/OWNER/tdd-guard</url>
    </repository>
</repositories>
```

And configure authentication in `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>github-tdd-guard</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_GITHUB_TOKEN</password>
    </server>
</servers>
```

## Activation

The reporter auto-enables when either condition is met:

1. **Environment variable**: `TDDGUARD_ENABLED=true`
2. **Directory exists**: `.claude/tdd-guard/` in project root

When disabled, all methods return immediately with <100µs overhead per test.

## Project Root Resolution

The reporter uses this fallback hierarchy to find the project root:

1. System property: `tddguard.projectRoot`
2. Environment variable: `TDDGUARD_PROJECT_ROOT`
3. Traverse up from working directory until finding `build.gradle`, `build.gradle.kts`, or `pom.xml`
4. Fallback to current working directory

## JSON Output Format

```json
{
  "framework": "junit5",
  "timestamp": "2025-11-06T12:00:00Z",
  "duration": 1500,
  "summary": {
    "total": 10,
    "passed": 8,
    "failed": 1,
    "skipped": 1
  },
  "tests": [
    {
      "name": "testMethod",
      "file": "src/test/java/com/example/MyTest.java",
      "status": "passed",
      "duration": 250,
      "displayName": "testMethod()"
    }
  ],
  "failures": [
    {
      "name": "failedTest",
      "file": "src/test/java/com/example/MyTest.java",
      "message": "Expected 5 but was 3",
      "stack": "java.lang.AssertionError: ..."
    }
  ],
  "educational": []
}
```

## Supported Test Types

- **Standard tests**: `@Test`
- **Nested tests**: `@Nested`
- **Parameterized tests**: `@ParameterizedTest`
- **Repeated tests**: `@RepeatedTest`
- **Dynamic tests**: `@TestFactory`
- **JUnit 4 Vintage**: Tests running through JUnit Vintage engine

## Implementation Details

### Architecture

- `TddGuardListener`: Main TestExecutionListener implementation
- `ProjectRootResolver`: Resolves project root using fallback hierarchy
- `TestResultCollector`: Captures test lifecycle events
- `TestJsonWriter`: Writes results to JSON with atomic file operations
- `model/`: POJO classes matching TDD Guard JSON schema

### Performance

- **Disabled**: <100µs overhead per test (single boolean check)
- **Enabled**: <1ms per test (event capture and collection)

### Error Handling

All exceptions are caught and logged to stderr without failing tests:

```
TDD Guard: Error writing test results: <message>
```

## Development

### Build

```bash
gradle build
```

### Test

```bash
gradle test
```

### Test Structure

- `src/test/java/.../`: Unit tests for individual components
- `src/test/java/.../integration/`: Integration tests with real JUnit Platform execution
- `src/test/java/.../integration/SampleTests.java`: Sample tests (passed, failed, skipped)
- `src/test/java/.../integration/AdvancedTestTypes.java`: Tests for @Parameterized, @Repeated, @TestFactory

## Requirements

- Java 11+ (target)
- Java 21 (build)
- JUnit Platform 1.10.1
- Gson 2.10.1

## License

MIT
