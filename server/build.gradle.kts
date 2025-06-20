plugins {
	id("com.diffplug.spotless")
}

allprojects {
	repositories {
		mavenCentral()
		maven("https://jitpack.io")
	}
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
	// optional: limit format enforcement to just the files changed by this feature branch
	// ratchetFrom "origin/main"

	format("misc") {
		// define the files to apply `misc` to
		target("*.gradle", "*.md", ".gitignore")

		// define the steps to apply to those files
		trimTrailingWhitespace()
		endWithNewline()
		leadingSpacesToTabs()
	}
	// format "yaml", {
	// 	target "*.yml", "*.yaml",

	// 	trimTrailingWhitespace()
	// 	endWithNewline()
	// 	indentWithSpaces(2)  // YAML cannot contain tabs: https://yaml.org/faq.html
	// }

	val editorConfig =
		mapOf(
			"max_line_length" to "off",
			"ktlint_experimental" to "enabled",
			"ktlint_standard_condition-wrapping" to "disabled",
			"ktlint_standard_property-naming" to "disabled",
			"ij_kotlin_packages_to_use_import_on_demand" to
				"java.util.*,kotlin.math.*,dev.slimevr.autobone.errors.*" +
				",io.github.axisangles.ktmath.*,kotlinx.atomicfu.*,kotlinx.coroutines.*" +
				",dev.slimevr.tracking.trackers.*,dev.slimevr.desktop.platform.ProtobufMessages.*" +
				",solarxr_protocol.rpc.*,kotlinx.coroutines.*,com.illposed.osc.*,android.app.*",
			"ij_kotlin_allow_trailing_comma" to true,
		)
	val ktlintVersion = "1.2.1"
	kotlinGradle {
		target("**/*.gradle.kts") // default target for kotlinGradle
		ktlint(ktlintVersion)
			.editorConfigOverride(editorConfig)
	}
	kotlin {
		target("**/*.kt")
		targetExclude("**/build/**/**.kt", "bin/")
		ktlint(ktlintVersion)
			.editorConfigOverride(editorConfig)
	}
	java {
		target("**/*.java")
		targetExclude("**/BuildConfig.java")

		removeUnusedImports()
		// Use eclipse JDT formatter
		eclipse()
			.configFile("spotless.xml")
			.withP2Mirrors(mapOf("https://download.eclipse.org/" to "https://mirror.umd.edu/eclipse/"))
	}
}
