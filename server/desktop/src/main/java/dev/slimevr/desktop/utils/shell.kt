package dev.slimevr.desktop.utils

import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

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
