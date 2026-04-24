import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.serialization") version "2.3.21"
	id("org.jetbrains.kotlin.plugin.compose") version "2.3.21"
	id("org.jetbrains.compose") version "1.10.3"
	id("com.gradleup.shadow")
	id("com.github.gmazzo.buildconfig") version "5.3.5"
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

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
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
		google()
		mavenCentral()
		maven(url = "https://jitpack.io")
		maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

dependencies {
	implementation(project(":server:core"))
	implementation("io.ktor:ktor-client-core:3.0.3")
	implementation("io.ktor:ktor-client-cio:3.0.3")
	implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
	implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
	implementation("commons-cli:commons-cli:1.8.0")
	implementation("org.apache.commons:commons-lang3:3.15.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
	implementation(compose.desktop.currentOs)
	implementation(compose.material3)
	implementation(compose.components.resources)
	implementation("io.coil-kt.coil3:coil-compose:3.4.0")


	testImplementation(kotlin("test"))
}

tasks.shadowJar {
	archiveBaseName.set("updater")
	archiveClassifier.set("")
	archiveVersion.set("")
}

compose.desktop {
	application {
		mainClass = "dev.slimevr.updater.MainKt"

		nativeDistributions {
			targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
			packageName = "SlimeVRUpdater"
			packageVersion = "1.0.0"
		}
	}
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

tasks.withType<JavaExec> {
	standardInput = System.`in`
	systemProperty("terminal.jline", "false")
}
