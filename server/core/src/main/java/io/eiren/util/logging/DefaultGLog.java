package io.eiren.util.logging;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DefaultGLog extends Thread implements IGLog {

	private final Logger logger;

	public static class LogEntry {

		private Level level;
		private String message;
		private Throwable t;

		public LogEntry(Level level, String message, Throwable t) {
			this(level, message);
			this.t = t;
		}

		public LogEntry(Level level, String message) {
			this.level = level;
			this.message = message;
			this.t = null;
		}

		public Level getLevel() {
			return level;
		}

		public String getMessage() {
			return message;
		}

		public Throwable getException() {
			return t;
		}
	}

	private final ArrayBlockingQueue<LogEntry> queue = new ArrayBlockingQueue<>(50000);
	private volatile LoggerRecorder recorder;

	@Override
	public void info(String message) {
		add(new LogEntry(Level.INFO, message));
	}

	@Override
	public void info(String message, Throwable t) {
		add(new LogEntry(Level.INFO, message, t));
	}

	@Override
	public void severe(String message) {
		add(new LogEntry(Level.SEVERE, message));
	}

	@Override
	public void severe(String message, Throwable t) {
		add(new LogEntry(Level.SEVERE, message, t));
	}

	@Override
	public void warning(String message) {
		add(new LogEntry(Level.WARNING, message));
	}

	@Override
	public void warning(String message, Throwable t) {
		add(new LogEntry(Level.WARNING, message, t));
	}

	@Override
	public void debug(String message) {
		add(new LogEntry(Level.INFO, "[DBG] " + message));
	}

	@Override
	public void debug(String message, Throwable t) {
		add(new LogEntry(Level.INFO, "[DBG] " + message, t));
	}

	@Override
	public void log(Level level, String message) {
		add(new LogEntry(level, message));
	}

	@Override
	public void log(Level level, String message, Throwable t) {
		add(new LogEntry(level, message, t));
	}

	private void add(LogEntry entry) {
		try {
			queue.put(entry);
		} catch (InterruptedException ignored) {}
		try {
			if (recorder != null)
				recorder.addEntry(entry);
		} catch (NullPointerException ignored) {}
	}

	@Override
	public void setRecorder(LoggerRecorder recorder) {
		this.recorder = recorder;
	}

	@Override
	public LoggerRecorder removeRecorder() {
		LoggerRecorder lr = this.recorder;
		this.recorder = null;
		return lr;
	}

	public DefaultGLog(Logger logger) {
		super("Logger");
		this.logger = logger;
		this.setDaemon(true);
		this.setPriority(7);
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				LogEntry log = queue.take();
				if (log.t != null)
					logger.log(log.level, log.message, log.t);
				else
					logger.log(log.level, log.message);
			} catch (InterruptedException ignored) {}
		}
	}
}
