package dev.slimevr.desktop

import dev.slimevr.logging.CONSOLE
import dev.slimevr.logging.configureLogging
import dev.slimevr.resolveLogDirectory
import io.klogging.config.SinkConfiguration
import io.klogging.rendering.RENDER_SIMPLE

/**
 * Logs to standard output and to the per-user log folder the launcher and the bug report tooling
 * already look in: `%AppData%`, `~/Library/Logs` or `$XDG_DATA_HOME` depending on the platform.
 */
suspend fun setupDesktopLogging() = configureLogging(
	consoleSink = SinkConfiguration(RENDER_SIMPLE, CONSOLE),
	logDirectory = resolveLogDirectory(),
)
