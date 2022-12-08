package dev.slimevr.config;

public class TapDetectionConfig {

	private float delay = 0.2f;
	private boolean enabled = true;

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
