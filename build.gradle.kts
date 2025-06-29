plugins {
    kotlin("jvm") version "2.1.21"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    id("com.diffplug.spotless") version "7.0.4"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "com.mjs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.apache.kafka:kafka-streams:3.9.1")
    implementation("io.confluent:kafka-streams-avro-serde:7.9.1")

    implementation("org.apache.avro:avro:1.12.0")

    implementation("io.klogging:slf4j-klogging:0.10.1")

    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.9.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("com.michaelstrasser:kotest-html-reporter:0.7.3")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val kotlinLicenseHeader = """/*
   Copyright 2023 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/    
"""

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("0.48.2")

        licenseHeader(kotlinLicenseHeader)

        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}
