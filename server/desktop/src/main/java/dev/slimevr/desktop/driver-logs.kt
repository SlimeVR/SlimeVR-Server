package dev.slimevr.desktop

import dev.slimevr.logging.BACKLOG_SIZE
import dev.slimevr.logging.LogFileEntry
import dev.slimevr.logging.LogSourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runInterruptible
import solarxr_protocol.rpc.LogEvent
import solarxr_protocol.rpc.LogLevel
import solarxr_protocol.rpc.LogSource
import java.io.RandomAccessFile
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.io.path.createDirectories

private const val BACKLOG_READ_BYTES = 256 * 1024L

// Driver: `[2026-10-15 23:09:49.011753034+0000] [INFO] message`
// Bindings provider: `[2026-10-07 03:56:11] [INFO] message`, local time
private val DRIVER_LINE = Regex("""^\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}(?:\.\d+)?)([+-]\d{4})?] \[([A-Z]+)] (.*)$""")
private val DRIVER_TIME: DateTimeFormatter = DateTimeFormatterBuilder()
	.appendPattern("yyyy-MM-dd HH:mm:ss")
	.optionalStart()
	.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
	.optionalEnd()
	.toFormatter()

class LogFileSource(override val source: LogSource, private val path: Path) : LogSourceProvider {
	override val events: Flow<LogEvent> = tailLogFile(source, path)

	override fun files(): List<LogFileEntry> {
		val file = path.toFile()
		if (!file.isFile) return emptyList()
		return listOf(
			LogFileEntry(
				name = file.name,
				size = file.length(),
				modifiedMs = file.lastModified(),
				current = true,
				open = { file.inputStream() },
			),
		)
	}
}

private fun tailLogFile(source: LogSource, path: Path): Flow<LogEvent> = flow {
	val directory = path.parent
	directory.createDirectories()

	FileSystems.getDefault().newWatchService().use { watcher ->
		directory.register(watcher, ENTRY_CREATE, ENTRY_MODIFY)

		var offset = maxOf(0L, fileSize(path) - BACKLOG_READ_BYTES)
		// The window usually starts mid-line; that cut line is skipped, a whole one is kept
		var dropFirstLine = offset > 0 && !startsLine(path, offset)
		var partial = ""

		fun readNewLines(): List<String> {
			if (fileSize(path) < offset) {
				offset = 0
				partial = ""
			}
			val (text, end) = readFrom(path, offset)
			offset = end
			val lines = (partial + text).split('\n')
			partial = lines.last()
			val complete = lines.dropLast(1)
			if (!dropFirstLine) return complete
			dropFirstLine = false
			return complete.drop(1)
		}

		readNewLines().takeLast(BACKLOG_SIZE).forEach { line -> lineEvent(source, line)?.let { emit(it) } }

		while (true) {
			val key = runInterruptible { watcher.take() }
			val touched = key.pollEvents().any { (it.context() as? Path)?.fileName == path.fileName }
			key.reset()
			if (!touched) continue
			readNewLines().forEach { line -> lineEvent(source, line)?.let { emit(it) } }
		}
	}
}.flowOn(Dispatchers.IO)

private fun fileSize(path: Path) = path.toFile().length()

private fun startsLine(path: Path, offset: Long): Boolean = RandomAccessFile(path.toFile(), "r").use {
	it.seek(offset - 1)
	it.read() == '\n'.code
}

/** Text from [offset] to the end of [path], and the offset it ends at. */
private fun readFrom(path: Path, offset: Long): Pair<String, Long> {
	val file = path.toFile()
	if (!file.isFile) return "" to 0L
	return RandomAccessFile(file, "r").use {
		val length = it.length()
		if (length <= offset) return@use "" to length
		it.seek(offset)
		val bytes = ByteArray((length - offset).toInt())
		it.readFully(bytes)
		bytes.toString(Charsets.UTF_8) to length
	}
}

private fun lineEvent(source: LogSource, raw: String): LogEvent? {
	val line = raw.trimEnd('\r')
	if (line.isEmpty()) return null

	val match = DRIVER_LINE.matchEntire(line)
		?: return LogEvent(source = source, timeMs = System.currentTimeMillis().toULong(), level = LogLevel.INFO, logger = "", message = line)

	val (time, offset, level, message) = match.destructured
	val zone = if (offset.isEmpty()) ZoneId.systemDefault() else ZoneOffset.of(offset)
	return LogEvent(
		source = source,
		timeMs = LocalDateTime.parse(time, DRIVER_TIME).atZone(zone).toInstant().toEpochMilli().toULong(),
		level = LogLevel.entries.firstOrNull { it.name == level } ?: LogLevel.INFO,
		logger = "",
		message = message,
	)
}
