import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.bmuschko.gradle.docker.tasks.image.*
import com.bmuschko.gradle.docker.tasks.container.*

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("com.bmuschko:gradle-docker-plugin:9.3.1")
	}
}

val junitVersion = "5.9.3"
val brotliVersion = "1.11.0"
val reactorVersion = "2022.0.6"

plugins {
	val kotlinVersion = "1.8.21"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	//kotlin("plugin.jpa") version "1.7.22"
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("org.graalvm.buildtools.native") version "0.9.22"
	id("com.bmuschko.docker-remote-api") version "9.3.1"
}

apply(plugin = "io.spring.dependency-management")

group = "de.akrebs.proto"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val dockerRepo = "axel9691"
val imageName = "edifact-reactive-server"

repositories {
	//maven { url 'https://repo.spring.io/snapshot' }
	maven {
		url = uri("https://repo.spring.io/milestone")
	}
	mavenCentral()
}

dependencies {
	//implementation("org.graalvm.internal:library-with-reflection:1.5")
	implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation(platform("io.projectreactor:reactor-bom:$reactorVersion"))
	implementation("io.projectreactor.netty:reactor-netty-core")
	implementation("io.netty:netty-all:4.1.92.Final")
	implementation ("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	//implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	//implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//runtimeOnly("com.h2database:h2")
	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
	testImplementation("org.junit.jupiter:junit-jupiter")

	// Java libs wanted by native image compiler
	implementation("com.github.luben:zstd-jni:1.5.5-2")
	implementation("org.conscrypt:conscrypt-openjdk:2.5.2")
	implementation("com.aayushatharva.brotli4j:brotli4j:$brotliVersion")
	runtimeOnly("com.aayushatharva.brotli4j:native-linux-x86_64:$brotliVersion")
	implementation("com.ning:compress-lzf:1.1.2")
	implementation("log4j:log4j:1.2.17")
	implementation("org.bouncycastle:bcprov-jdk16:1.46")
	implementation("org.lz4:lz4-java:1.8.0")
	testImplementation("org.hamcrest:hamcrest-all:1.3")

}

//sourceSets.main {
//	java.srcDirs.add(file("./src/main/java"))
//	kotlin.srcDirs.add(file("./src/main/kotlin"))
//}

//tasks.withType<Jar> {
//	from(sourceSets.main)
//	from(sourceSets.main.kotlin)
//}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.create("copySources", Copy::class)  {
	from("./src/main")
	into("./build/docker/src/main")
}

tasks.create("copyScripts", Copy::class)  {
	// well.. memento this script copies itself to the Docker build directory! This wouldn't
	// be necessary if the Docker plugin could use the project's root directory..
	from("./build.gradle.kts", "./settings.gradle.kts", "./gradle.properties")
	into("build/docker")
}

tasks.create("copyDockerfile", Copy::class) {
	from("./Dockerfile")
	into("./build/docker")
}

tasks.create("prepareDockerBuild") {
	dependsOn("copySources", "copyScripts", "copyDockerfile")
}

tasks.create("createDockerImage", DockerBuildImage::class) {
	dependsOn("prepareDockerBuild")
	inputDir.set(file("build/docker"))
	images.add("${dockerRepo}/${imageName}:${project.version}")
}

docker {
	//url.set("https://localhost:2375")
	//certPath.set(File(System.getProperty("user.home"), ".boot2docker/certs/boot2docker-vm"))

	registryCredentials {
		url.set("https://hub.docker.com/")
		username.set("axel9691krebs")
		password.set("Axel#9691")
		email.set("axelkrebs@online.de")
	}
}

graalvmNative {
	binaries.all {
		resources.autodetect()
	}
	toolchainDetection.set(false)
}
