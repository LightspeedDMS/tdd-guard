plugins {
    `java-library`
    `maven-publish`
}

group = "com.lightspeed.tddguard"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    // Build with Java 21 for optimizations
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit Platform API for TestExecutionListener
    compileOnly("org.junit.platform:junit-platform-launcher:1.10.1")

    // JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.10.1")
}

tasks.test {
    useJUnitPlatform()

    // Configure test execution
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:unchecked")
    options.compilerArgs.add("-Xlint:deprecation")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "junit5"
        }
    }
}
