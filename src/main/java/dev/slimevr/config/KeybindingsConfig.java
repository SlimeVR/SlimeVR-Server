package dev.slimevr.config;

public class KeybindingsConfig {

	private String resetBinding = "CTRL+ALT+SHIFT+Y";

	private String quickResetBinding = "CTRL+ALT+SHIFT+U";

	private long resetDelay = 0L;

	private long quickResetDelay = 0L;

	public KeybindingsConfig() {
	}

	public String getResetBinding() {
		return resetBinding;
	}

	public String getQuickResetBinding() {
		return quickResetBinding;
	}

	public long getResetDelay() {
		return resetDelay;
	}

	public void setResetDelay(long time) {
		resetDelay = time;
	}

	public long getQuickResetDelay() {
		return quickResetDelay;
	}

	public void setQuickResetDelay(long time) {
		quickResetDelay = time;
	}
}
