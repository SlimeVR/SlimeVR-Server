package io.eiren.util.logging;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;


public class FileLogHandler extends StreamHandler {

	protected class DatedLogFile implements Comparable<DatedLogFile> {
		public final File file;
		public final LocalDateTime dateTime;
		public final int count;

		protected DatedLogFile(File file, LocalDateTime dateTime, int count) {
			this.file = file;
			this.dateTime = dateTime;
			this.count = count;
		}

		@Override
		public int compareTo(@NotNull DatedLogFile o) {
			int dtCompare = dateTime.compareTo(o.dateTime);
			return dtCompare != 0 ? dtCompare : Integer.compare(count, o.count);
		}
	}

	private final String logSuffix = ".log";

	private final ArrayList<DatedLogFile> logFiles;

	private final Path path;
	private final String logTag;
	private final DateTimeFormatter dateFormat;
	private final LocalDateTime dateTime;
	private final String date;
	private final int limit;
	private final int maxCount;

	private DataOutputStream curStream;
	private int fileCount = 0;

	public FileLogHandler(
		Path path,
		String logTag,
		DateTimeFormatter dateFormat,
		int limit,
		int count
	) {
		this.path = path;
		this.logTag = logTag;
		this.dateFormat = dateFormat;
		this.dateTime = LocalDateTime.now();
		this.date = dateTime.format(dateFormat);
		this.limit = limit;
		this.maxCount = count;

		logFiles = findLogs(path);

		newFile();
	}

	private synchronized void deleteFile(File file) {
		if (!file.delete()) {
			file.deleteOnExit();
			reportError(
				"Failed to delete file, deleting on exit.",
				null,
				ErrorManager.GENERIC_FAILURE
			);
		}
	}

	private DatedLogFile parseFileName(File file) {
		String name = file.getName();

		// Log name should have at least two '_', one integer, and at least one
		// char for the datetime (4 chars)
		if (
			!name.startsWith(logTag)
				|| !name.endsWith(logSuffix)
				|| name.length() < (logTag.length() + logSuffix.length() + 4)
		) {
			// Ignore non-matching files
			return null;
		}

		int dateEnd = name.lastIndexOf('_');
		if (dateEnd < 0) {
			// Ignore non-matching files
			return null;
		}

		// Move past the tag, then between the two '_'
		String dateTimeStr = name.substring(logTag.length() + 1, dateEnd);
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dateFormat);

		// Move past the date '_' and behind the suffix
		int logNum = Integer.parseInt(name, dateEnd + 1, name.length() - logSuffix.length(), 10);

		return new DatedLogFile(file, dateTime, logNum);
	}

	private ArrayList<DatedLogFile> findLogs(Path path) {
		ArrayList<DatedLogFile> logFiles = new ArrayList<>();

		File[] files = path.toFile().listFiles();
		if (files == null)
			return logFiles;

		for (File log : files) {
			DatedLogFile parsedFile = parseFileName(log);
			if (parsedFile != null) {
				logFiles.add(parsedFile);
			}
		}

		return logFiles;
	}

	private synchronized void deleteEarliestFile() {
		DatedLogFile earliest = null;

		for (DatedLogFile log : logFiles) {
			if (earliest == null || log.compareTo(earliest) < 0) {
				earliest = log;
			}
		}

		if (earliest != null) {
			logFiles.remove(earliest);
			deleteFile(earliest.file);
		}
	}

	private synchronized void newFile() {
		// Delete files over the count
		while (logFiles.size() >= maxCount) {
			deleteEarliestFile();
		}

		try {
			Path logPath = path.resolve(logTag + "_" + date + "_" + fileCount + logSuffix);
			File newFile = logPath.toFile();

			curStream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(newFile))
			);
			setOutputStream(curStream);

			logFiles.add(new DatedLogFile(newFile, dateTime, fileCount));
			fileCount += 1;
		} catch (FileNotFoundException e) {
			reportError(null, e, ErrorManager.OPEN_FAILURE);
		}
	}

	@Override
	public synchronized void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}

		super.publish(record);
		flush();

		if (limit > 0 && curStream.size() >= limit) {
			newFile();
		}
	}
}