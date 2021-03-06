import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin ("jvm") version "1.6.10"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "co.petrin"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

val vertxVersion = "4.3.1"
val junitJupiterVersion = "5.7.0"

//val mainVerticleName = "co.petrin.ekristijan.MainVerticle"
//val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/main/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set("co.petrin.ekristijan.StartupKt")
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-config:${vertxVersion}")
  runtimeOnly("io.vertx:vertx-config-hocon:$vertxVersion")
  implementation(kotlin("stdlib-jdk8"))
  implementation("nl.martijndwars:web-push:5.1.1")
  implementation("org.bouncycastle:bcprov-jdk15on:1.70") // required for web push
  testImplementation("io.vertx:vertx-junit5")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.11.4")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.2")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  implementation("com.github.ntrrgc:ts-generator:1.1.1") // generate typescript definition files
  runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
  implementation("info.picocli:picocli:4.6.1")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "17"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("--config", "$projectDir/src/main/resources/config.hocon")
}

buildscript {
  repositories {

    mavenCentral()
  }
  dependencies {

  }
}

tasks.create<JavaExec>(name = "generateDTO") {
  group = "build"
  description = "Generate TypeScript definitions of DTO classes"
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("co.petrin.ekristijan.dto.GenerateDTOKt")
  args = listOf(file("$projectDir/src/frontend/dto.d.ts").absolutePath)
}