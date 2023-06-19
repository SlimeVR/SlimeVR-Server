plugins {
	`java-library`
	id("robovm")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

allprojects {
	repositories {
		mavenCentral()
		maven {
			url = uri("https://oss.sonatype.org/content/repositories/snapshots")
		}
	}
}

dependencies {
	val robovmVersion = "10.2.2-SNAPSHOT"

	implementation(project(":server:core"))
	implementation("com.robovmx:robovm-rt:$robovmVersion")
	implementation("com.robovmx:robovm-cocoatouch:$robovmVersion")
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
tasks.createIPA {
	dependsOn(tasks.build)
}

robovm {
	isIosSkipSigning = true
}
