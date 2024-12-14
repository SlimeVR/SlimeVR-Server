package io.eiren.util.logging;

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

	private final String logSuffix = ".log";

	private final ArrayList<File> logFiles = new ArrayList<>();

	private final Path path;
	private final String logTag;
	private final String date;
	private final int limit;
	private final int maxCount;

	private DataOutputStream curStream;
	private int fileCount = 0;

	public FileLogHandler(
		String path,
		String logTag,
		DateTimeFormatter dateFormat,
		int limit,
		int count
	) {
		this.path = Path.of(path);
		this.logTag = logTag;
		this.date = LocalDateTime.now().format(dateFormat);
		this.limit = limit;
		this.maxCount = count;

		File[] pathFiles = this.path.toFile().listFiles();
		if (pathFiles != null) {
			for (File file : pathFiles) {
				String name = file.getName();
				if (name.startsWith(logTag) && name.endsWith(logSuffix)) {
					logFiles.add(file);
				}
			}
		}

		newFile();
	}

	private synchronized void deleteOldestFile() {
		long oldestTime = Long.MAX_VALUE;
		File oldestFile = null;
		for (File file : logFiles) {
			long lastModified = file.lastModified();
			if (lastModified < oldestTime) {
				oldestTime = lastModified;
				oldestFile = file;
			}
		}

		if (oldestFile != null) {
			logFiles.remove(oldestFile);
			if (!oldestFile.delete()) {
				oldestFile.deleteOnExit();
			}
		}
	}

	private synchronized void newFile() {
		// Delete files over the count
		while (logFiles.size() >= maxCount) {
			deleteOldestFile();
		}

		try {
			Path logPath = path.resolve(logTag + "_" + date + "_" + fileCount + logSuffix);
			File newFile = logPath.toFile();
			curStream = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(newFile))
			);
			setOutputStream(curStream);

			logFiles.add(newFile);
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

		if (limit > 0 && curStream.size() >= limit) {
			newFile();
		}
	}
}
