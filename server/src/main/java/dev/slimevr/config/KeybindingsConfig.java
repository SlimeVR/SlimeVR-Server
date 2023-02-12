package dev.slimevr.config;

public class KeybindingsConfig {

	private String resetBinding = "CTRL+ALT+SHIFT+Y";

	private String quickResetBinding = "CTRL+ALT+SHIFT+U";

	private String resetMountingBinding = "CTRL+ALT+SHIFT+I";

	private String tapFeedBackSoundBinding = "CTRL+ALT+SHIFT+O";

	private long resetDelay = 0L;

	private long quickResetDelay = 0L;

	private long resetMountingDelay = 0L;


	public KeybindingsConfig() {
	}

	public String getResetBinding() {
		return resetBinding;
	}

	public String getQuickResetBinding() {
		return quickResetBinding;
	}

	public String getResetMountingBinding() {
		return resetMountingBinding;
	}

	public String getTapFeedBackSoundBinding() {
		return tapFeedBackSoundBinding;
	}

	public long getResetDelay() {
		return resetDelay;
	}

	public void setResetDelay(long delay) {
		resetDelay = delay;
	}

	public long getQuickResetDelay() {
		return quickResetDelay;
	}

	public void setQuickResetDelay(long delay) {
		quickResetDelay = delay;
	}

	public long getResetMountingDelay() {
		return resetMountingDelay;
	}

	public void setResetMountingDelay(long delay) {
		resetMountingDelay = delay;
	}
}
