package io.eiren.util.logging;

import java.util.logging.Level;


public interface IGLog {

	public void info(String message);

	public void severe(String message);

	public void warning(String message);

	public void debug(String message);

	public default void info(String message, Throwable t) {
		log(Level.INFO, message, t);
	}

	public default void severe(String message, Throwable t) {
		log(Level.SEVERE, message, t);
	}

	public default void warning(String message, Throwable t) {
		log(Level.WARNING, message, t);
	}

	public default void debug(String message, Throwable t) {
		log(Level.INFO, "[DBG] " + message, t);
	}

	public void log(Level level, String message);

	public void log(Level level, String message, Throwable t);

	public void setRecorder(LoggerRecorder recorder);

	public LoggerRecorder removeRecorder();

	static class GLevel extends Level {

		private static final long serialVersionUID = -539856764608026895L;

		private GLevel(String s, int i) {
			super(s, i);
		}
	}
}
