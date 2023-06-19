package dev.slimevr.ios.logging;

import java.util.logging.ConsoleHandler;

public class FoundationConsoleHandler extends ConsoleHandler {
	public FoundationConsoleHandler() {
		super();

		super.setOutputStream(new FoundationLogPrintStream());
	}
}
