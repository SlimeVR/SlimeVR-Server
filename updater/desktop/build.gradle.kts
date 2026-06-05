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
		freeCompilerArgs.add("-Xskip-prerelease-check")
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}
tasks.withType<Test> {
	systemProperty("file.encoding", "UTF-8")
}
tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

group = "dev.slimevr"
version = "updater"

repositories {
	google()
	mavenCentral()
	maven(url = "https://jitpack.io")
	maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
	implementation("io.ktor:ktor-client-core:3.0.3")
	implementation("io.ktor:ktor-client-cio:3.0.3")
	implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
	implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
	implementation("commons-cli:commons-cli:1.8.0")
	implementation("org.apache.commons:commons-lang3:3.18.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	implementation(compose.desktop.currentOs)
	implementation("org.jetbrains.compose.material3:material3:1.9.0-beta03")
	implementation("org.jetbrains.compose.components:components-resources:1.10.3")
	implementation("org.jetbrains.compose.components:components-animatedimage:1.10.3")

	implementation("net.java.dev.jna:jna:5.14.0")
	implementation("net.java.dev.jna:jna-platform:5.14.0")

	implementation("com.github.ajalt.mordant:mordant:3.0.2")
	implementation("com.github.ajalt.mordant:mordant-markdown:3.0.2")

	testImplementation("org.slf4j:slf4j-simple:2.0.9")
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.20")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.x")
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}

compose.desktop {
	application {
		mainClass = "dev.slimevr.updater.MainKt"

		nativeDistributions {
			includeAllModules = false
			modules(
				"java.base",
				"java.desktop",
				"java.instrument",
				"java.naming",
				"java.prefs",
				"jdk.unsupported"
			)
			targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
			packageName = "SlimeVR-Updater"
			packageVersion = "1.0.0"
			vendor = "Nighty Electronics"


			windows {
				console = true
				menuGroup = "SlimeVR"
				perUserInstall = true
				shortcut = true
				iconFile.set(project.file("src/main/resources/icon.ico"))
			}
		}
		buildTypes.release {
			proguard {
				isEnabled = true
				configurationFiles.from(project.file("compose-proguard-rules.pro"))
			}
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

tasks.shadowJar {
	archiveBaseName.set("desktop-updater")
	archiveClassifier.set("")
	archiveVersion.set("")
	manifest {
		attributes["Main-Class"] = "dev.slimevr.updater.MainKt"
	}
}
