package dev.slimevr.desktop.install

import dev.slimevr.AppLogger
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.FeatureFlags
import dev.slimevr.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun runInstaller() {
	when (CURRENT_PLATFORM) {
		Platform.LINUX -> installLinux()
		Platform.WINDOWS -> installWindows()
		else -> AppLogger.install.warn("Updater doesn't support operating system '$CURRENT_PLATFORM'")
	}
}

suspend fun executeShellCommand(vararg command: String): Pair<Int, String>? = try {
	withContext(Dispatchers.IO) {
		val process = ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()

		val output = process.inputStream.bufferedReader().readText()
		process.waitFor()
		Pair(process.exitValue(), output)
	}
} catch (e: IOException) {
	AppLogger.install.warn("Error executing shell command: ${e.message}")
	null
}

// vrpathreg returns 1 if the driver is not present
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
