package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.io.IOException

fun executeShellCommand(vararg command: String): Pair<Int, String>? = try {
	val process = ProcessBuilder(*command)
		.redirectErrorStream(true)
		.start()

	process.waitFor()
	Pair(process.exitValue(), process.inputStream.bufferedReader().readText())
} catch (e: IOException) {
	LogManager.warning("Error executing shell command", e)
	null
}

// vrpathreg returns 1 if the driver is present
fun shouldInstallDriver(exitCode: Int): Boolean = exitCode == 1

// get a descriptive reason why we're skipping driver installation
// Exit codes from vrpathreg output:
//    0 : Success
//    1 : ( finddriver only ) Driver not present
//    2 : ( finddriver only ) Error, driver installed more than once
//   -1 : Configuration or permission problem
//   -2 : Argument problem
fun getDriverInstallSkipReason(exitCode: Int): String = when (exitCode) {
	0 -> "driver already installed"
	2 -> "driver installed more than once"
	-1 -> "SteamVR is misconfigured"
	else -> "unknown reason ($exitCode)"
}
