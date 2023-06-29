buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.20.2")
	}
}

plugins {
	kotlin("jvm") version "1.8.21"
}

subprojects {
	plugins.apply("kotlinx-atomicfu")
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
}
repositories {
	mavenCentral()
}
kotlin {
	jvmToolchain(17)
}
