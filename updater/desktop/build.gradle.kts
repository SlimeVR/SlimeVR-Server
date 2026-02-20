import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization") version "2.3.10"
	application
	id("com.gradleup.shadow")
	id("com.github.gmazzo.buildconfig")
	id("org.ajoberstar.grgit")
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}
tasks.withType<KotlinCompile> {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
		freeCompilerArgs.set(listOf("-Xvalue-classes"))
	}
}

// Set compiler to use UTF-8
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}
tasks.withType<Test> {
	systemProperty("file.encoding", "UTF-8")
}
tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

group = "org.example"
version = "unspecified"

allprojects {
	repositories {
		// Use jcenter for resolving dependencies.
		// You can declare any Maven/Ivy/file repository here.
		mavenCentral()
		maven(url = "https://jitpack.io")
		maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
	}
}

dependencies {
	implementation(project(":server:core"))
	implementation("io.ktor:ktor-client-core:3.0.3")
	implementation("io.ktor:ktor-client-cio:3.0.3")
	implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
	implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
	implementation("org.apache.commons:commons-lang3:3.20.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.shadowJar {
	minimize {
		exclude(dependency("com.fazecast:jSerialComm:.*"))
		exclude(dependency("net.java.dev.jna:.*:.*"))
		exclude(dependency("com.google.flatbuffers:flatbuffers-java:.*"))

		exclude(project(":solarxr-protocol"))
	}
	archiveBaseName.set("updater")
	archiveClassifier.set("")
	archiveVersion.set("")
}

application {
	mainClass.set("dev.slimevr.updater.Main")
}

buildConfig {
	useKotlinOutput { topLevelConstants = true }
	packageName("dev.slimevr.updater")

	val gitVersionTag = providers.exec {
		commandLine("git", "--no-pager", "tag", "--sort", "-taggerdate", "--points-at", "HEAD")
	}.standardOutput.asText.get().split('\n').first()
	buildConfigField("String", "GIT_COMMIT_HASH", "\"${grgit.head().abbreviatedId}\"")
	buildConfigField("String", "GIT_VERSION_TAG", "\"${gitVersionTag.trim()}\"")
	buildConfigField("boolean", "GIT_CLEAN", grgit.status().isClean.toString())
}

tasks.run<JavaExec> {
	standardInput = System.`in` // this is not working
	args = listOf("run")
}
