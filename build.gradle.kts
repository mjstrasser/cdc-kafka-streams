import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.6.0"
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
