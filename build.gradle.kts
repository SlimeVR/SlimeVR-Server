buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.20.2")
	}
}

plugins {
	kotlin("jvm") apply false
}

subprojects {
	plugins.apply("kotlinx-atomicfu")
}

repositories {
	mavenCentral()
}

