import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.6.0"
    id("com.diffplug.spotless") version "6.16.0"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
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

    implementation("org.apache.kafka:kafka-streams:3.4.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.3.2")

    implementation("org.apache.avro:avro:1.11.1")

    implementation("io.klogging:slf4j-klogging:0.3.0")

    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.4.0")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("com.michaelstrasser:kotest-html-reporter:0.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
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
        indentWithSpaces()
        endWithNewline()
    }
}