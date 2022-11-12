package io.eiren.util.logging;

import java.util.logging.Level;


public interface IGLog {

	void info(String message);

	void severe(String message);

	void warning(String message);

	void debug(String message);

	default void info(String message, Throwable t) {
		log(Level.INFO, message, t);
	}

	default void severe(String message, Throwable t) {
		log(Level.SEVERE, message, t);
	}

	default void warning(String message, Throwable t) {
		log(Level.WARNING, message, t);
	}

	default void debug(String message, Throwable t) {
		log(Level.INFO, "[DBG] " + message, t);
	}

	void log(Level level, String message);

	void log(Level level, String message, Throwable t);

	void setRecorder(LoggerRecorder recorder);

	LoggerRecorder removeRecorder();

	class GLevel extends Level {

		private static final long serialVersionUID = -539856764608026895L;

		private GLevel(String s, int i) {
			super(s, i);
		}
	}
}
