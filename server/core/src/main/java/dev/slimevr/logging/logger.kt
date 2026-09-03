package dev.slimevr.logging

import io.klogging.Level
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import io.klogging.noCoLogger
import io.klogging.rendering.RENDER_SIMPLE
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

	val coroutines = noCoLogger("Coroutines")
	val console = noCoLogger("Console")

	object ShouldDebug {
		var skeletonTicks = false
		var eventDispatcher = false
	}
}

private const val CONSOLE_SINK = "console"
private const val FILE_SINK = "file"

suspend fun configureLogging(consoleSink: SinkConfiguration, logDirectory: Path?, minLevel: Level = Level.INFO) {
	val fileSender = logDirectory?.let { runCatching { LogFileSender(it) } }

	loggingConfiguration {
		sink(CONSOLE_SINK, consoleSink)
		fileSender?.getOrNull()?.let { sink(FILE_SINK, RENDER_SIMPLE, it) }
		logging {
			fromMinLevel(minLevel) {
				toSink(CONSOLE_SINK)
				if (fileSender?.isSuccess == true) toSink(FILE_SINK)
			}
		}
	}

	captureStandardStreams()

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
}
