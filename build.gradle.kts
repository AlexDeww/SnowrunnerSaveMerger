import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    kotlin("multiplatform") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.home"
version = "1.0-SNAPSHOT"

val jvmEntryPoint = "org.home.MainKt"
val nativeEntryPoint = "org.home.main"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(22)

    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        binaries {
            executable {
                mainClass.set(jvmEntryPoint)
            }
        }
    }

    mingwX64("windows") {
        binaries {
            executable {
                entryPoint(nativeEntryPoint)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.8.2")

                val serialization = "1.7.3"
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-io:$serialization")
            }
        }
        val jvmMain by getting
        val windowsMain by getting
    }

    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
    sourceSets.all {
        languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
    }
}

tasks.register<ShadowJar>("shadowJar") {
    val jvmTarget = kotlin.targets.getByName("jvm") as KotlinJvmTarget
    from(jvmTarget.compilations.getByName("main").output)

    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))

    manifest {
        attributes["Main-Class"] = jvmEntryPoint
    }

    archiveClassifier.set("all")
}
