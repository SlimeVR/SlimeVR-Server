package io.eiren.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class ShortConsoleLogFormatter extends Formatter {

	protected final SimpleDateFormat date;

	public ShortConsoleLogFormatter() {
		this.date = createDateFormat();
	}

	protected SimpleDateFormat createDateFormat() {
		return new SimpleDateFormat("HH:mm:ss");
	}

	protected void buildMessage(StringBuilder builder, LogRecord record) {
		builder.append(date.format(record.getMillis()));
		builder.append(" [");
		builder.append(record.getLevel().getLocalizedName().toUpperCase());
		builder.append("] ");
		builder.append(record.getMessage());
		builder.append('\n');
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		Throwable ex = record.getThrown();

		buildMessage(builder, record);

		if (ex != null) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			builder.append(writer);
		}

		String message = builder.toString();
		Object[] parameters = record.getParameters();
		if (parameters == null || parameters.length == 0)
			return message;
		if (
			message.contains("{0")
				|| message.contains("{1")
				|| message.contains("{2")
				|| message.contains("{3")
		)
			return java.text.MessageFormat.format(message, parameters);
		return message;
	}

}
