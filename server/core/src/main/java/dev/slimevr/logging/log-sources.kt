package dev.slimevr.logging

import kotlinx.coroutines.flow.Flow
import solarxr_protocol.rpc.LogEvent
import solarxr_protocol.rpc.LogSource
import java.io.InputStream

class LogFileEntry(
	val name: String,
	val size: Long,
	val modifiedMs: Long,
	val current: Boolean,
	val open: () -> InputStream,
)

/** One place logs can be read from: this server, the OpenVR driver... */
interface LogSourceProvider {
	val source: LogSource

	/** Read on every request; a file name is only valid while it is in this list */
	fun files(): List<LogFileEntry>

	/**
	 * Starts with the events kept from before the subscription, oldest first, then follows new
	 * ones. Null when the source only has files.
	 */
	val events: Flow<LogEvent>?
}
