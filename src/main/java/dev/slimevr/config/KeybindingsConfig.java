package dev.slimevr.config;

public class KeybindingsConfig {

	private String resetBinding = "CTRL+ALT+SHIFT+Y";

	private String quickResetBinding = "CTRL+ALT+SHIFT+U";

	private String resetMountingBinding = "CTRL+ALT+SHIFT+I";

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
}
