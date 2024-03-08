plugins {
    kotlin("jvm") version "1.9.23"
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
    testImplementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter)
    implementation(libs.kotlin.serialization)
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
