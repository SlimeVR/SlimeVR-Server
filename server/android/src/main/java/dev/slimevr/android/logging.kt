package dev.slimevr.android

import android.util.Log
import dev.slimevr.logging.configureLogging
import io.klogging.Level
import io.klogging.config.SinkConfiguration
import io.klogging.rendering.RenderString
import io.klogging.rendering.evalTemplate
import io.klogging.sending.EventSender
import java.io.File

private val RENDER_LOGCAT = RenderString { event ->
	val items = if (event.items.isNotEmpty()) " : ${event.items}" else ""
	val stackTrace = event.stackTrace?.let { "\n$it" } ?: ""
	event.evalTemplate() + items + stackTrace
}

private val LOGCAT = EventSender { batch ->
	for (event in batch) {
		val priority = when (event.level) {
			Level.TRACE -> Log.VERBOSE
			Level.DEBUG -> Log.DEBUG
			Level.INFO, Level.NONE -> Log.INFO
			Level.WARN -> Log.WARN
			Level.ERROR, Level.FATAL -> Log.ERROR
		}
		Log.println(priority, event.logger, RENDER_LOGCAT(event))
	}
}

suspend fun setupAndroidLogging(filesDir: File) = configureLogging(
	consoleSink = SinkConfiguration(eventSender = LOGCAT),
	logDirectory = filesDir.toPath(),
	minLevel = Level.DEBUG,
)
