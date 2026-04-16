package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.io.IOException

fun executeShellCommand(vararg command: String): String? = try {
	val process = ProcessBuilder(*command)
		.redirectErrorStream(true)
		.start()
	process.inputStream.bufferedReader().readText().also {
		process.waitFor()
	}
} catch (e: IOException) {
	LogManager.warning("Error executing shell command", e)
	null
}
