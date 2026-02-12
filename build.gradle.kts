import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "me.doghouse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jsoup:jsoup:1.15.3")
}

kotlin {
    jvmToolchain(23)
}

application {
    mainClass.set("MainKt")
}