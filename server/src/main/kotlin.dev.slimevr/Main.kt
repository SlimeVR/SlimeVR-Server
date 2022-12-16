@file:JvmName("Main")

package dev.slimevr

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import java.lang.System

const val VERSION: String = (if (GIT_VERSION_TAG.isEmpty()) GIT_COMMIT_HASH else GIT_VERSION_TAG) + if (GIT_CLEAN) "" else "-dirty"
var vrServer: VRServer? = null

fun main(args: Array<String>) {
    System.setProperty("awt.useSystemAAFontSettings", "on")
    System.setProperty("swing.aatext", "true")

    val parser: CommandLineParser = DefaultParser()
    val formatter = HelpFormatter()
    val options = Options()
    val help = Option("h", "help", false, "Show help")
    val version = Option("V", "version", false, "Show version")
    options.addOption(help)
    options.addOption(version)
    val cmd: CommandLine = try {
        parser.parse(options, args, true)
    } catch (e: org.apache.commons.cli.ParseException) {
        println(e.message)
        formatter.printHelp("slimevr.jar", options)
        java.lang.System.exit(1)
    }
    if (cmd.hasOption("help")) {
        formatter.printHelp("slimevr.jar", options)
        java.lang.System.exit(0)
    }
    if (cmd.hasOption("version")) {
        println("SlimeVR Server " + VERSION)
        java.lang.System.exit(0)
    }
    val dir: java.io.File = java.io.File("").getAbsoluteFile()
    try {
        io.eiren.util.logging.LogManager.initialize(java.io.File(dir, "logs/"), dir)
    } catch (e1: java.lang.Exception) {
        e1.printStackTrace()
    }
    io.eiren.util.logging.LogManager.info("Running version " + VERSION)
    if (!org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast(org.apache.commons.lang3.JavaVersion.JAVA_17)) {
        io.eiren.util.logging.LogManager.severe("SlimeVR start-up error! A minimum of Java 17 is required.")
        JOptionPane
            .showMessageDialog(
                null,
                "SlimeVR start-up error! A minimum of Java 17 is required.",
                "SlimeVR: Java Runtime Mismatch",
                JOptionPane.ERROR_MESSAGE
            )
        return
    }
    try {
        // This is disabled because the config can't be read at this point
        // new ServerSocket(6969).close();
        ServerSocket(35903).close()
        ServerSocket(21110).close()
    } catch (e: IOException) {
        io.eiren.util.logging.LogManager
            .severe(
                "SlimeVR start-up error! Required ports are busy. Make sure there is no other instance of SlimeVR Server running."
            )
        JOptionPane
            .showMessageDialog(
                null,
                "SlimeVR start-up error! Required ports are busy. Make sure there is no other instance of SlimeVR Server running.",
                "SlimeVR: Ports are busy",
                JOptionPane.ERROR_MESSAGE
            )
        return
    }
    try {
        vrServer = VRServer()
        vrServer.start()
        Keybinding(vrServer)
    } catch (e: Throwable) {
        e.printStackTrace()
        try {
            java.lang.Thread.sleep(2000L)
        } catch (e2: InterruptedException) {
            e.printStackTrace()
        }
        java.lang.System.exit(1) // Exit in case error happened on init and window
        // not appeared, but some thread
        // started
    } finally {
        try {
            java.lang.Thread.sleep(2000L)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
