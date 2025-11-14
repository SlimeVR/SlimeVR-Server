package io.eiren.util.logging

import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.ErrorManager
import java.util.logging.LogRecord
import java.util.logging.StreamHandler

/**
 * A log handler that manages a set of log files, deleting the oldest log(s) when limits
 * are reached.
 * @param path The directory to store and discover the log files in.
 * @param logTag The identifier tag for log files (ex. "slimevr-server").
 * @param dateFormat The format to use for the date and time in the log file names.
 * @param limit The independent log file size limit in bytes.
 * @param maxCount The collective log file count limit.
 * @param collectiveLimit The collective log file size limit in bytes.
 */
class FileLogHandler @JvmOverloads constructor(
	private val path: Path,
	private val logTag: String,
	private val dateFormat: DateTimeFormatter,
	private val limit: Int,
	private val maxCount: Int,
	private val collectiveLimit: Long = -1,
) : StreamHandler() {

	/**
	 * A log [File] with a [LocalDateTime] and [count] associated to it.
	 */
	inner class DatedLogFile(val file: File, val dateTime: LocalDateTime, val count: Int) : Comparable<DatedLogFile> {
		override fun compareTo(o: DatedLogFile): Int {
			val dtCompare = dateTime.compareTo(o.dateTime)
			return if (dtCompare != 0) dtCompare else count.compareTo(o.count)
		}
	}

	/**
	 * The symbol to use for separating file name sections.
	 */
	private val sectionSeparator = '_'

	/**
	 * The file suffix to use for logs.
	 */
	private val logSuffix = ".log"

	/**
	 * The list of log files being managed by this [FileLogHandler].
	 */
	private val logFiles: MutableList<DatedLogFile>

	/**
	 * The [LocalDateTime] to use for log files created by this [FileLogHandler]
	 * instance.
	 */
	private val dateTime: LocalDateTime = LocalDateTime.now()

	/**
	 * The formatted string representing [dateTime].
	 */
	private val date: String = dateTime.format(dateFormat)

	/**
	 * The current file [DataOutputStream].
	 */
	private var curStream: DataOutputStream? = null

	/**
	 * The current file count for logs based on the same [dateTime] made by this
	 * [FileLogHandler] instance.
	 */
	private var fileCount = 0

	/**
	 * The current collective size of all log files in bytes.
	 */
	private var collectiveSize: Long = 0

	init {
		// Find old logs to manage
		logFiles = findLogs(path)
		if (collectiveLimit > 0) {
			collectiveSize = sumFileSizes(logFiles)
		}

		// Create new log and delete over the count
		newFile()
	}

	/**
	 * Parses [file]'s name and returns a parsed [DatedLogFile].
	 * @param file The file to parse the name of.
	 * @return The parsed [DatedLogFile].
	 */
	private fun parseFileName(file: File): DatedLogFile? {
		val name = file.getName()

		// Log name should have at least two separators, one integer, and at
		// least one char for the datetime (4 chars)
		if (!name.startsWith(logTag) || !name.endsWith(logSuffix) || name.length < (logTag.length + logSuffix.length + 4)) {
			// Ignore non-matching files
			return null
		}

		val dateEnd = name.lastIndexOf(sectionSeparator)
		if (dateEnd < 0) {
			// Ignore non-matching files
			return null
		}

		try {
			// Move past the tag, then between the two separators
			val dateTimeStr = name.substring(logTag.length + 1, dateEnd)
			val dateTime = LocalDateTime.parse(dateTimeStr, dateFormat)

			// Move past the date separator and behind the suffix

			val logNum = name.substring(dateEnd + 1, name.length - logSuffix.length).toInt()

			return DatedLogFile(file, dateTime, logNum)
		} catch (_: Exception) {
			// Unable to parse log file, probably not valid
			return null
		}
	}

	/**
	 * Finds all the log files parseable by [parseFileName] in the provided path,
	 * [path].
	 * @param path The path to check for log files.
	 * @return A list containing all parsed [DatedLogFile]s from [path].
	 */
	private fun findLogs(path: Path): MutableList<DatedLogFile> {
		val logFiles = mutableListOf<DatedLogFile>()

		val files = path.toFile().listFiles()
		if (files == null) return logFiles

		// Find all parseable log files
		for (log in files) {
			val parsedFile = parseFileName(log)
			if (parsedFile != null) {
				logFiles.add(parsedFile)
			}
		}

		return logFiles
	}

	/**
	 * Computes the sum of the file sizes from the provided [logFiles].
	 * @param logFiles The list of [DatedLogFile]s to compute the size of.
	 * @return The size of all the provided [DatedLogFile]s in bytes.
	 */
	private fun sumFileSizes(logFiles: List<DatedLogFile>): Long {
		var size: Long = 0
		for (log in logFiles) {
			size += log.file.length()
		}
		return size
	}

	/**
	 * Tries to delete the provided [file].
	 * @param file The [File] to delete.
	 */
	private fun deleteFile(file: File) {
		if (file.delete()) return

		file.deleteOnExit()
		reportError(
			"Failed to delete file, deleting on exit.",
			null,
			ErrorManager.GENERIC_FAILURE,
		)
	}

	/**
	 * Returns the earliest [DatedLogFile] from the provided list, [logFiles].
	 * @param logFiles The [DatedLogFile]s to find the earliest file from.
	 * @return The earliest [DatedLogFile] or null if none could be found.
	 */
	private fun getEarliestFile(logFiles: List<DatedLogFile>): DatedLogFile? {
		var earliest: DatedLogFile? = null

		for (log in logFiles) {
			if (earliest == null || log < earliest) {
				earliest = log
			}
		}

		return earliest
	}

	/**
	 * Deletes the earliest log file from [logFiles].
	 */
	@Synchronized
	private fun deleteEarliestFile() {
		val earliest = getEarliestFile(logFiles)
		if (earliest == null) return

		// If we have a collective limit, update the current size and clamp
		if (collectiveLimit > 0) {
			collectiveSize = (
				collectiveSize - earliest.file.length()
				).coerceAtLeast(0)
		}

		logFiles.remove(earliest)
		deleteFile(earliest.file)
	}

	/**
	 * Deletes the earliest log files until the [collectiveSize] plus [curFileSize] is
	 * less than [collectiveLimit].
	 * @param curFileSize The size of the current log file in bytes.
	 */
	@Synchronized
	private fun deleteOverCollectiveLimit(curFileSize: Int = 0) {
		if (collectiveLimit <= 0) return

		// Delete files over the collective size limit, including the current stream
		while (!logFiles.isEmpty() && collectiveSize + curFileSize >= collectiveLimit) {
			deleteEarliestFile()
		}
	}

	/**
	 * Creates a new log file, closing [curStream] automatically and incrementing
	 * [fileCount]. This method also handles deleting old log files based on the
	 * specified limits: [limit], [maxCount], and [collectiveLimit].
	 */
	@Synchronized
	private fun newFile() {
		// Clear the last log file
		val lastStream = curStream
		if (lastStream != null) {
			// Flush the log first
			close()
			// Then accumulate the amount written after
			collectiveSize += lastStream.size()
		}

		if (maxCount > 0) {
			// Delete files over the count
			while (logFiles.size >= maxCount) {
				deleteEarliestFile()
			}
		}

		// Handle collective limit
		deleteOverCollectiveLimit()

		try {
			val newFile = path.resolve(
				logTag + sectionSeparator + date + sectionSeparator + fileCount + logSuffix,
			).toFile()

			// Use DataOutputStream to count bytes written
			val newStream = DataOutputStream(
				BufferedOutputStream(FileOutputStream(newFile)),
			)
			// Closes the last stream automatically if not already done
			setOutputStream(newStream)
			curStream = newStream

			// Add log to the tracking list to be deleted if needed
			logFiles.add(DatedLogFile(newFile, dateTime, fileCount))
			fileCount += 1
		} catch (e: FileNotFoundException) {
			reportError(null, e, ErrorManager.OPEN_FAILURE)
		}
	}

	/**
	 * Publishes the provided [record]. This automatically manages deleting old log
	 * files based on [collectiveLimit] as the new log is written and creating new log
	 * files after the file size limit, [limit], is reached.
	 */
	@Synchronized
	override fun publish(record: LogRecord?) {
		if (!isLoggable(record)) return

		// Push the log
		super.publish(record)
		// Then flush, so we always have the latest output
		flush()

		// The number of bytes written to the current log file
		val curFileSize = curStream!!.size()

		// Handle the collective limit as we write to the new log file
		deleteOverCollectiveLimit(curFileSize)

		// If written above the log limit, make a new file
		if (limit > 0 && curFileSize >= limit) {
			newFile()
		}
	}
}
