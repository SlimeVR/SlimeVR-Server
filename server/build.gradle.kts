plugins {
	id("com.diffplug.spotless")
}

repositories {
	mavenCentral()
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
		indentWithTabs()
	}
	// format "yaml", {
	// 	target "*.yml", "*.yaml",

	// 	trimTrailingWhitespace()
	// 	endWithNewline()
	// 	indentWithSpaces(2)  // YAML cannot contain tabs: https://yaml.org/faq.html
	// }

	// .editorconfig doesn't work so, manual override
	// https://github.com/diffplug/spotless/issues/142
	val editorConfig =
		mapOf(
			"indent_size" to 4,
			"indent_style" to "tab",
// 			"max_line_length" to 88,
			"ktlint_experimental" to "enabled",
			"ij_kotlin_packages_to_use_import_on_demand" to
				"java.util.*,kotlin.math.*,dev.slimevr.autobone.errors.*" +
				",io.github.axisangles.ktmath.*,kotlinx.atomicfu.*" +
				",dev.slimevr.tracking.trackers.*,dev.slimevr.desktop.platform.ProtobufMessages.*",
			"ij_kotlin_allow_trailing_comma" to true
		)
	val ktlintVersion = "0.47.1"
	kotlinGradle {
		target("**/*.gradle.kts") // default target for kotlinGradle
		ktlint(ktlintVersion)
			.setUseExperimental(true)
			.editorConfigOverride(editorConfig)
	}
	kotlin {
		target("**/*.kt")
		targetExclude("**/build/**/**.kt")
		ktlint(ktlintVersion)
			.setUseExperimental(true)
			.editorConfigOverride(editorConfig)
	}
	java {
		target("**/*.java")
		targetExclude("**/BuildConfig.java")

		removeUnusedImports()
		// Use eclipse JDT formatter
		eclipse().configFile("spotless.xml")
	}
}
