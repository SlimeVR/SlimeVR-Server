package dev.slimevr.updater

import Manifest
import ManifestUtils.Companion.getChannels
import ManifestUtils.Companion.getVersionTags
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles.bold
import dev.slimevr.updater.util.TerminalUtil
import dev.slimevr.updater.util.TerminalUtil.t
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import kotlin.system.exitProcess

val VERSION = (GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) + if (GIT_CLEAN) "" else "-dirty"

val featureFlags = FeatureFlags()

val manifestPath = "update-manifest.json"

fun main(args: Array<String>) {
	val options = Options().apply {
		addOption("h", "help", false, "Show help")
		addOption("c", "channels", false, "List all release channels")
		addOption(
			Option.builder("i")
				.longOpt("install")
				.hasArgs()
				.numberOfArgs(2)
				.argName("channel> <version")
				.desc("Specify channel and version to update to")
				.build(),
		)
		addOption("l", "list", true, "List all versions for a channel")
		addOption("r", "restart-server", false, "Restart server after the updater is done")
	}

	val parser: CommandLineParser = DefaultParser()
	val formatter = HelpFormatter()

	val cmd: CommandLine = try {
		parser.parse(options, args)
	} catch (e: ParseException) {
		TerminalUtil.error("Argument Error: ${e.message}")
		formatter.printHelp("updater.jar", options)
		exitProcess(1)
	}

	t.println(bold(TextColors.cyan("SlimeVR Updater — Version $VERSION")))
	t.println(TextColors.gray("━".repeat(t.size.width.coerceAtMost(40))))

	if (cmd.hasOption("help")) {
		formatter.printHelp("updater.jar", options)
		exitProcess(0)
	}

	if (cmd.hasOption("channels")) {
		val manifest = Manifest(manifestPath).getManifest()
		val channels = getChannels(manifest)
		TerminalUtil.info("Available Release Channels:")
		channels.forEach { t.println(" • ${TextColors.green(it)}") }
		exitProcess(0)
	}

	if (cmd.hasOption("list")) {
		val channel = cmd.getOptionValue("list")
		val manifest = Manifest(manifestPath).getManifest()
		val versions = getVersionTags(manifest, channel)
		TerminalUtil.printVersionGrid(versions, title = "Releases in '$channel'")
		exitProcess(0)
	}

	if (cmd.hasOption("install")) {
		val installArgs = cmd.getOptionValues("install")
		if (installArgs.size != 2) {
			TerminalUtil.error("Argument Error: usage <channel> <version>")
			exitProcess(1)
		}
		val channel = installArgs[0]
		val version = installArgs[1]
		featureFlags.version = version
		featureFlags.channel = channel

		TerminalUtil.success("Target set: ${bold(version)} on channel ${bold(channel)}")
	}

	if (cmd.hasOption("restart-server")) {
		featureFlags.restartServer = true
	}

	TerminalUtil.info("Launching graphical interface...")
	val updaterController = UpdaterController()
	updaterController.startGui()
}
