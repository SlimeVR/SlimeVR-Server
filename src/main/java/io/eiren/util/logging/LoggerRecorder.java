package io.eiren.util.logging;

import java.util.List;

import io.eiren.util.collections.FastList;
import io.eiren.util.logging.DefaultGLog.LogEntry;


public class LoggerRecorder {

	private final List<LogEntry> recorded = new FastList<>();

	public LoggerRecorder() {
	}

	public synchronized void addEntry(LogEntry e) {
		recorded.add(e);
	}

	public List<LogEntry> getEntries() {
		return recorded;
	}
}
