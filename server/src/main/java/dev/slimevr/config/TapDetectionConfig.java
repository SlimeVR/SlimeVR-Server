package dev.slimevr.config;

public class TapDetectionConfig {

	private float delay = 0.2f;
	private boolean quickResetEnabled = true;
	private boolean resetEnabled = true;
	private boolean mountingResetEnabled = true;

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public boolean getQuickResetEnabled() {
		return quickResetEnabled;
	}

	public void setQuickResetEnabled(boolean quickResetEnabled) {
		this.quickResetEnabled = quickResetEnabled;
	}

	public boolean getResetEnabled() {
		return resetEnabled;
	}

	public void setResetEnabled(boolean resetEnabled) {
		this.resetEnabled = resetEnabled;
	}

	public boolean getMountingResetEnabled() {
		return mountingResetEnabled;
	}

	public void setMountingResetEnabled(boolean mountingResetEnabled) {
		this.mountingResetEnabled = mountingResetEnabled;
	}


}
