package dev.slimevr.logging

import dev.slimevr.sentry.reportLog
import io.klogging.Level
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import io.klogging.noCoLogger
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.rendering.RenderString
import io.klogging.sending.EventSender
import kotlinx.coroutines.flow.Flow
import solarxr_protocol.rpc.LogEvent
import solarxr_protocol.rpc.LogSource
import java.nio.file.Path

object AppLogger {
	val tracker = logger("Tracker")
	val device = logger("Device")
	val udp = logger("UDPConnection")
	val ipc = logger("IPC")
	val solarxr = logger("SolarXR")
	val checklist = logger("Checklist")
	val steamvr = logger("SteamVR")
	val hid = logger("HID")
	val serial = logger("Serial")
	val firmware = logger("Firmware")
	val vrc = logger("VRChat")
	val bvh = logger("BVH")
	val vmc = logger("VMC")
	val oscQuery = logger("OSCQuery")
	val install = logger("Install")
	val resets = logger("Resets")
	val keybind = logger("Keybind")
	val config = logger("Config")
	val skeleton = logger("Skeleton")
	val networkProfile = logger("NetworkProfile")
	val logging = logger("Logging")
	val events = logger("Events")
	val stayAligned = logger("Stay Aligned")
	val stepMounting = logger("Step Mounting")

	val coroutines = noCoLogger("Coroutines")
	val console = noCoLogger("Console")

	// For the UDP receive thread, which blocks on the socket and handles each datagram inline. A
	// suspend logger would need a coroutine there, and resuming one per packet costs more than the
	// packet's own work. Same name as udp logger, so its lines read as the rest of the UDP logs
	val udpReceive = noCoLogger("UDPConnection")

	object ShouldDebug {
		var skeletonTicks = false
		var eventDispatcher = false
	}
}

/** RENDER_SIMPLE without terminal colour codes, so the files are plain text. */
private val RENDER_FILE = RenderString { RENDER_SIMPLE(it).replace(ANSI_ESCAPE, "") }

private const val CONSOLE_SINK = "console"
private const val FILE_SINK = "file"
private const val ERROR_REPORTS_SINK = "error-reports"
private const val LOG_PAGE_SINK = "log-page"

private val errorReportsSender = EventSender { batch ->
	batch.forEach { reportLog(it.logger, it.level, it.message, it.stackTrace) }
}

class ServerLogSource(
	buffer: LogEventBuffer,
	private val logDirectory: Path?,
) : LogSourceProvider {
	override val source = LogSource.SLIMEVR_SERVER

	override val events: Flow<LogEvent> = buffer.events

	override fun files(): List<LogFileEntry> {
		val directory = logDirectory ?: return emptyList()
		return listLogFiles(directory).mapIndexed { index, file ->
			LogFileEntry(
				name = file.name,
				size = file.length(),
				modifiedMs = file.lastModified(),
				current = index == 0,
				open = { file.inputStream() },
			)
		}
	}
}

suspend fun configureLogging(consoleSink: SinkConfiguration, logDirectory: Path?, minLevel: Level = Level.INFO): ServerLogSource {
	val fileSender = logDirectory?.let { runCatching { LogFileSender(it) } }
	val logPageBuffer = LogEventBuffer(LogSource.SLIMEVR_SERVER)

	loggingConfiguration {
		sink(CONSOLE_SINK, consoleSink)
		fileSender?.getOrNull()?.let { sink(FILE_SINK, RENDER_FILE, it) }
		sink(ERROR_REPORTS_SINK, SinkConfiguration(eventSender = errorReportsSender))
		sink(LOG_PAGE_SINK, SinkConfiguration(eventSender = logPageBuffer))
		logging {
			fromMinLevel(minLevel) {
				toSink(CONSOLE_SINK)
				if (fileSender?.isSuccess == true) toSink(FILE_SINK)
				toSink(LOG_PAGE_SINK)
			}
			fromMinLevel(Level.WARN) { toSink(ERROR_REPORTS_SINK) }
		}
	}

	val failure = fileSender?.exceptionOrNull()
	when {
		fileSender == null ->
			AppLogger.logging
				.warn("No log folder for this platform, logging to the console only")

		failure != null ->
			AppLogger.logging
				.error(failure, "Cannot write logs in $logDirectory, logging to the console only")

		else -> AppLogger.logging.info("Writing logs to $logDirectory")
	}

	captureStandardStreams()

	return ServerLogSource(logPageBuffer, logDirectory?.takeIf { fileSender?.isSuccess == true })
}
