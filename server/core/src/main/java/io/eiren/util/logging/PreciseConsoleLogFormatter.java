package io.eiren.util.logging;

import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;


/**
 * Format message timestamp as time passed from the start with milliseconds.
 */
public class PreciseConsoleLogFormatter extends ShortConsoleLogFormatter {

	private final long startMills;

	public PreciseConsoleLogFormatter() {
		startMills = System.currentTimeMillis();
	}

	@Override
	protected SimpleDateFormat createDateFormat() {
		return new SimpleDateFormat("mm:ss.SSS");
	}

	@Override
	protected void buildMessage(StringBuilder builder, LogRecord record) {
		builder.append(date.format(record.getMillis() - startMills));
		builder.append(" [");
		builder.append(record.getLevel().getLocalizedName().toUpperCase());
		builder.append("] ");
		builder.append(record.getMessage());
		builder.append('\n');
	}
}
