@file:JvmName("Main")

package dev.slimevr

import io.eiren.util.logging.LogManager
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.IOException
import java.lang.System
import java.net.ServerSocket
import javax.swing.JOptionPane
import kotlin.concurrent.thread
import kotlin.system.exitProcess

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"
lateinit var vrServer: VRServer
	private set

fun main(args: Array<String>) {
	System.setProperty("awt.useSystemAAFontSettings", "on")
	System.setProperty("swing.aatext", "true")

	val parser: CommandLineParser = DefaultParser()
	val formatter = HelpFormatter()
	val options = Options()
	options.addOption("h", "help", false, "Show help")
	options.addOption("V", "version", false, "Show version")
	val cmd: CommandLine = try {
		parser.parse(options, args, true)
	} catch (e: org.apache.commons.cli.ParseException) {
		formatter.printHelp("slimevr.jar", options)
		exitProcess(1)
	}

	if (cmd.hasOption("help")) {
		formatter.printHelp("slimevr.jar", options)
		exitProcess(0)
	}
	if (cmd.hasOption("version")) {
		println("SlimeVR Server $VERSION")
		exitProcess(0)
	}

	if (cmd.args.isEmpty()) {
		System.err.println("No command specified, expected 'run'")
		exitProcess(1)
	}
	if (!cmd.args[0].equals("run", true)) {
		System.err.println("Unknown command: ${cmd.args[0]}, expected 'run'")
		exitProcess(1)
	}

	val dir = File("").absoluteFile
	try {
		LogManager.initialize(dir)
	} catch (e1: java.lang.Exception) {
		e1.printStackTrace()
	}
	LogManager.info("Running version $VERSION")
	if (!SystemUtils.isJavaVersionAtLeast(org.apache.commons.lang3.JavaVersion.JAVA_17)) {
		LogManager.severe("SlimeVR start-up error! A minimum of Java 17 is required.")
		JOptionPane
			.showMessageDialog(
				null,
				"SlimeVR start-up error! A minimum of Java 17 is required.",
				"SlimeVR: Java Runtime Mismatch",
				JOptionPane.ERROR_MESSAGE
			)
		LogManager.closeLogger()
		return
	}
	try {
		// This is disabled because the config can't be read at this point
		// new ServerSocket(6969).close();
		ServerSocket(35903).close()
		ServerSocket(21110).close()
	} catch (e: IOException) {
		LogManager
			.severe(
				"SlimeVR start-up error! Required ports are busy. " +
					"Make sure there is no other instance of SlimeVR Server running."
			)
		JOptionPane
			.showMessageDialog(
				null,
				"SlimeVR start-up error! Required ports are busy. " +
					"Make sure there is no other instance of SlimeVR Server running.",
				"SlimeVR: Ports are busy",
				JOptionPane.ERROR_MESSAGE
			)
		LogManager.closeLogger()
		return
	}
	try {
		vrServer = VRServer()
		vrServer.start()
		Keybinding(vrServer)
		val scanner = thread {
			while (true) {
				if (readln() == "exit") {
					vrServer.interrupt()
					break
				}
			}
		}
		vrServer.join()
		scanner.join()
		LogManager.closeLogger()
		exitProcess(0)
	} catch (e: Throwable) {
		e.printStackTrace()
		exitProcess(1)
	}
}
