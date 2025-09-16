plugins {
	`java-library`
	id("robovm")
	id("org.ajoberstar.grgit")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

allprojects {
	repositories {
		mavenCentral()
// 		mavenLocal()
		maven(url = "https://jitpack.io")
		maven {
			url = uri("https://central.sonatype.com/repository/maven-snapshots/")
		}
	}
}

dependencies {
	val robovmVersion = rootProject.properties["robovmVersion"] as String

	implementation(project(":server:core"))
	implementation("com.robovmx:robovm-rt:$robovmVersion")
	implementation("com.robovmx:robovm-cocoatouch:$robovmVersion")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.13.5")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.5")
	implementation("org.slf4j:slf4j-simple:2.0.7")
}

tasks.launchIPhoneSimulator {
	dependsOn(tasks.build)
}
tasks.launchIPadSimulator {
	dependsOn(tasks.build)
}
tasks.launchIOSDevice {
	dependsOn(tasks.build)
}
tasks.robovmArchive {
	dependsOn(tasks.build)
}

tasks.register("makeLocalProperties") {
	File("${project.projectDir}/robovm.local.properties").writeText(
		"""
		app.version=${grgit.describe(mapOf("tags" to true, "always" to true))}
		app.build=${grgit.tag.list().size}
		""".trimIndent(),
	)
}

tasks.build {
	dependsOn(":server:ios:makeLocalProperties")
}

robovm {
	isIosSkipSigning = true
}
