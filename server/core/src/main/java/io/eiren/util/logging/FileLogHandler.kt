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

class FileLogHandler @JvmOverloads constructor(
	private val path: Path,
	private val logTag: String,
	private val dateFormat: DateTimeFormatter,
	private val limit: Int,
	private val maxCount: Int,
	private val collectiveLimit: Long = -1,
) : StreamHandler() {

	inner class DatedLogFile(val file: File, val dateTime: LocalDateTime, val count: Int) : Comparable<DatedLogFile> {
		override fun compareTo(o: DatedLogFile): Int {
			val dtCompare = dateTime.compareTo(o.dateTime)
			return if (dtCompare != 0) dtCompare else count.compareTo(o.count)
		}
	}

	private val sectionSeparator = '_'
	private val logSuffix = ".log"

	private val logFiles: ArrayList<DatedLogFile>

	private val dateTime: LocalDateTime = LocalDateTime.now()
	private val date: String = dateTime.format(dateFormat)

	private var curStream: DataOutputStream? = null
	private var fileCount = 0
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

	private fun parseFileName(file: File): DatedLogFile? {
		val name = file.getName()

		// Log name should have at least two separators, one integer, and at
		// least one char for the datetime (4 chars)
		if (!name.startsWith(logTag) || !name.endsWith(logSuffix) || name.length < (logTag.length + logSuffix.length + 4)
		) {
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

	private fun findLogs(path: Path): ArrayList<DatedLogFile> {
		val logFiles: ArrayList<DatedLogFile> = ArrayList<DatedLogFile>()

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

	private fun sumFileSizes(logFiles: ArrayList<DatedLogFile>): Long {
		var size: Long = 0
		for (log in logFiles) {
			size += log.file.length()
		}
		return size
	}

	private fun deleteFile(file: File) {
		if (!file.delete()) {
			file.deleteOnExit()
			reportError(
				"Failed to delete file, deleting on exit.",
				null,
				ErrorManager.GENERIC_FAILURE,
			)
		}
	}

	private fun getEarliestFile(logFiles: ArrayList<DatedLogFile>): DatedLogFile? {
		var earliest: DatedLogFile? = null

		for (log in logFiles) {
			if (earliest == null || log < earliest) {
				earliest = log
			}
		}

		return earliest
	}

	@Synchronized
	private fun deleteEarliestFile() {
		val earliest = getEarliestFile(logFiles)
		if (earliest != null) {
			// If we have a collective limit, update the current size and clamp
			if (collectiveLimit > 0) {
				collectiveSize -= earliest.file.length()
				if (collectiveSize < 0) collectiveSize = 0
			}

			logFiles.remove(earliest)
			deleteFile(earliest.file)
		}
	}

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

		if (collectiveLimit > 0) {
			// Delete files over the collective size limit
			while (!logFiles.isEmpty() && collectiveSize >= collectiveLimit) {
				deleteEarliestFile()
			}
		}

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

	@Synchronized
	override fun publish(record: LogRecord?) {
		if (!isLoggable(record)) {
			return
		}

		// Push the log
		super.publish(record)
		// Then flush, so we always have the latest output
		flush()

		if (collectiveLimit > 0) {
			// Delete files over the collective size limit, including the current stream
			while (!logFiles.isEmpty() && collectiveSize + curStream!!.size() >= collectiveLimit) {
				deleteEarliestFile()
			}
		}

		// If written above the log limit, make a new file
		if (limit > 0 && curStream!!.size() >= limit) {
			newFile()
		}
	}
}
