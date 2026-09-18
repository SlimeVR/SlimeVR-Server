package dev.slimevr.logging

import io.klogging.sending.SendString
import java.io.BufferedOutputStream
import java.io.Closeable
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createDirectories

/** Prefix of every log file name, also used to recognise logs left by previous runs. */
private const val LOG_TAG = "slimevr-server"

private const val LOG_SUFFIX = ".log"

/** Separates the tag, the timestamp and the index in a log file name. */
private const val SEPARATOR = '_'

private val FILE_NAME_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")

/** Size at which the current file is closed and a new one started, in bytes. */
private const val FILE_SIZE_LIMIT = 12 * 1000 * 1000

/** How many log files are kept, across all runs. Times [FILE_SIZE_LIMIT], this caps the folder. */
private const val MAX_FILE_COUNT = 8

/**
 * Writes rendered log events to `slimevr-server_<date>_<index>.log` files in [directory], starting
 * a new one once the current file passes [fileSizeLimit] and keeping only the [maxFileCount] most
 * recent, logs left by previous runs included.
 */
class LogFileSender(
	private val directory: Path,
	private val fileSizeLimit: Int = FILE_SIZE_LIMIT,
	private val maxFileCount: Int = MAX_FILE_COUNT,
) : SendString,
	Closeable {

	private val namePrefix = "$LOG_TAG$SEPARATOR${LocalDateTime.now().format(FILE_NAME_DATE_FORMAT)}$SEPARATOR"

	private var index = 0
	private var stream = openNextFile()

	@Synchronized
	override fun invoke(eventString: String) {
		if (stream.size() >= fileSizeLimit) {
			stream.close()
			stream = openNextFile()
		}

		stream.write("$eventString\n".toByteArray())
		stream.flush()
	}

	@Synchronized
	override fun close() = stream.close()

	private fun openNextFile(): DataOutputStream {
		directory.createDirectories()
		deleteLogsBeyond(directory, keep = maxFileCount - 1)

		val file = directory.resolve("$namePrefix${index++}$LOG_SUFFIX").toFile()
		return DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
	}
}

private fun deleteLogsBeyond(directory: Path, keep: Int) = directory.toFile()
	.listFiles().orEmpty()
	.filter { it.name.startsWith(LOG_TAG) && it.name.endsWith(LOG_SUFFIX) }
	.sortedByDescending { it.lastModified() }
	.drop(keep)
	// A file another process still holds open cannot be deleted on Windows; retry on exit.
	.forEach { if (!it.delete()) it.deleteOnExit() }
