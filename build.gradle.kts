plugins {
	id("org.ajoberstar.grgit")
}

buildscript {
	repositories {
		google()
		mavenCentral()
	}
	dependencies {
		classpath("com.android.tools.build:gradle:8.6.1")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.properties["kotlinVersion"]}")
	}
}

subprojects.filter { it.name.contains("tauri") }.forEach {
	it.repositories {
		mavenCentral()
		google()
	}
}
