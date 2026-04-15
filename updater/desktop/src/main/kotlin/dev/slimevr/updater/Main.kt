@file:JvmName("Main")

package dev.slimevr.updater

import dev.slimevr.updater.ManifestUtils.Companion.listChannels
import dev.slimevr.updater.ManifestUtils.Companion.listVersions
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import kotlin.system.exitProcess
import kotlin.text.ifEmpty

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"
val updaterGui = UpdaterGui()

val featureFlags = FeatureFlags()

fun main(args: Array<String>) {
	val parser: CommandLineParser = DefaultParser()
	val formatter = HelpFormatter()
	val options = Options()
	options.addOption("h", "help", false, "Show help")
	options.addOption("i", "install", false, "Specify version to update to")
	options.addOption("c", "channels", false, "List all release channels")
	options.addOption("l", "list", false, "List all versions")
	val cmd: CommandLine = try {
		parser.parse(options, args, true)
	} catch (e: org.apache.commons.cli.ParseException) {
		formatter.printHelp("updater.jar", options)
		exitProcess(1)
	}
	if (cmd.hasOption("help")) {
		formatter.printHelp("updater.jar", options)
		exitProcess(0)
	}
	if (cmd.hasOption("install")) {
		featureFlags.version = cmd.getOptionValue("install")
	}
	if (cmd.hasOption("channels")) {
		featureFlags.listChannels = true
		val manifest = Manifest().getManifest()
		listChannels(manifest)
		exitProcess(0)
	}
	if (cmd.hasOption("list")) {
		featureFlags.listVersions = true
		val manifest = Manifest().getManifest()
		listVersions(manifest, cmd.getOptionValue("list"))
		exitProcess(0)
	}

	val manifest = Manifest()

	val updater = Updater()
	updater.runUpdater()
}
