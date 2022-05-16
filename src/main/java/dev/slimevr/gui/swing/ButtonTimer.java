package dev.slimevr.gui.swing;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;


public class ButtonTimer {

	private static final Timer timer = new Timer();

	public static void runTimer(
		AbstractButton button,
		int seconds,
		String defaultText,
		Runnable runnable
	) {
		if (seconds <= 0) {
			button.setText(defaultText);
			runnable.run();
		} else {
			button.setText(String.valueOf(seconds));
			timer.schedule(new ButtonTimerTask(button, seconds - 1, defaultText, runnable), 1000);
		}
	}

	private static class ButtonTimerTask extends TimerTask {

		private final AbstractButton button;
		private final int seconds;
		private final String defaultText;
		private final Runnable runnable;

		private ButtonTimerTask(
			AbstractButton button,
			int seconds,
			String defaultText,
			Runnable runnable
		) {
			this.button = button;
			this.seconds = seconds;
			this.defaultText = defaultText;
			this.runnable = runnable;
		}

		@Override
		public void run() {
			runTimer(button, seconds, defaultText, runnable);
		}
	}
}
