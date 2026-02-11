import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    application
}

group = "me.doghouse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jsoup:jsoup:1.15.2")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClass.set("MainKt")
}