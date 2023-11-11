plugins {
    kotlin("jvm") version "1.9.20"
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.filter)
    alias(libs.plugins.versions.update)
}

version = "2023"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.assertions.core)
    implementation(libs.junit.jupiter.api)
    implementation(libs.kotlin.serialization)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "1g"
    maxHeapSize = "10g"
    testLogging.showStandardStreams = true
    filter {
        setIncludePatterns("UtilKtTest", "GraphTest", "Day0*", "Day1*", "Day2*")
    }
}

kotlin {
    jvmToolchain(21)
}
