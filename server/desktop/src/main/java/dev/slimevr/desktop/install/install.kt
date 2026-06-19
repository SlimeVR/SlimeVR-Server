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

suspend fun executeShellCommand(vararg command: String): String? = try {
	val process = withContext(Dispatchers.IO) {
		ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()
	}
	process.inputStream.bufferedReader().readText().also {
		process.waitFor()
	}
} catch (e: IOException) {
	AppLogger.install.warn("Error executing shell command: ${e.message}")
	null
}
