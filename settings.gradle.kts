pluginManagement {
    plugins {
        id("org.graalvm.buildtools.native") version "0.9.22"
        id("io.spring.dependency-management") version "1.1.0"
        id("org.springframework.boot") version "3.0.6"
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
            metadataSources {
                mavenPom()
                artifact()
            }
        }
    }
}
rootProject.name = "edifact-reactive-server"
