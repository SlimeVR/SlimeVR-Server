package io.eiren.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;


public class LoggerOutputStream extends ByteArrayOutputStream {

	private static final String separator = System.getProperty("line.separator");

	private final IGLog logger;
	private final Level level;
	private final String prefix;
	private final StringBuilder buffer = new StringBuilder();

	public LoggerOutputStream(IGLog logger, Level level) {
		this(logger, level, "");
	}

	public LoggerOutputStream(IGLog logger, Level level, String prefix) {
		super();
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
	}

	@Override
	public void flush() throws IOException {
		synchronized (this) {
			super.flush();
			String record = this.toString();
			super.reset();
			if (record.length() > 0) {
				buffer.append(record);
				if (record.contains(separator)) {
					String s = buffer.toString();
					String[] split = s.split(separator);
					for (String value : split)
						logger.log(level, prefix + value);
					buffer.setLength(0);
					// buffer.append(split[split.length - 1]);
				}
			}
		}
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}
