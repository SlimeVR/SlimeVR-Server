package dev.slimevr.config;

public class KeybindingsConfig {

	private String fullResetBinding = "CTRL+ALT+SHIFT+Y";

	private String yawResetBinding = "CTRL+ALT+SHIFT+U";

	private String mountingResetBinding = "CTRL+ALT+SHIFT+I";

	private String pauseTrackingBinding = "CTRL+ALT+SHIFT+O";

	private long fullResetDelay = 0L;

	private long yawResetDelay = 0L;

	private long mountingResetDelay = 0L;

	private long pauseTrackingDelay = 0L;


	public KeybindingsConfig() {
	}

	public String getFullResetBinding() {
		return fullResetBinding;
	}

	public String getYawResetBinding() {
		return yawResetBinding;
	}

	public String getMountingResetBinding() {
		return mountingResetBinding;
	}

	public String getPauseTrackingBinding() {
		return pauseTrackingBinding;
	}

	public long getFullResetDelay() {
		return fullResetDelay;
	}

	public void setFullResetDelay(long delay) {
		fullResetDelay = delay;
	}

	public long getYawResetDelay() {
		return yawResetDelay;
	}

	public void setYawResetDelay(long delay) {
		yawResetDelay = delay;
	}

	public long getMountingResetDelay() {
		return mountingResetDelay;
	}

	public void setMountingResetDelay(long delay) {
		mountingResetDelay = delay;
	}

	public long getPauseTrackingDelay() {
		return pauseTrackingDelay;
	}

	public void setPauseTrackingDelay(long delay) {
		pauseTrackingDelay = delay;
	}
}
