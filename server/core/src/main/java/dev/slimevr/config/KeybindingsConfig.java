package dev.slimevr.config;

import dev.slimevr.Keybinding.KeybindName;

public class KeybindingsConfig {

	private String fullResetBinding = "CTRL+ALT+SHIFT+Y";

	private String yawResetBinding = "CTRL+ALT+SHIFT+U";

	private String mountingResetBinding = "CTRL+ALT+SHIFT+I";

	private String feetMountingResetBinding = "CTRL+ALT+SHIFT+P";

	private String pauseTrackingBinding = "CTRL+ALT+SHIFT+O";

	private float fullResetDelay = 0L;

	private float yawResetDelay = 0L;

	private float mountingResetDelay = 0L;

	private float feetMountingResetDelay = 0L;

	private float pauseTrackingDelay = 0L;

	public KeybindingsConfig() {}

	public String getFullResetBinding() {
		return fullResetBinding;
	}

	public void setFullResetBinding(String fullResetBinding) {
		this.fullResetBinding = fullResetBinding;
	}

	public String getYawResetBinding() {
		return yawResetBinding;
	}

	public void setYawResetBinding(String yawResetBinding) {
		this.yawResetBinding = yawResetBinding;
	}

	public String getMountingResetBinding() {
		return mountingResetBinding;
	}

	public void setMountingResetBinding(String mountingResetBinding) {
		this.mountingResetBinding = mountingResetBinding;
	}

	public String getFeetMountingResetBinding() {
		return feetMountingResetBinding;
	}

	public void setFeetMountingResetBinding(String feetMountingResetBinding) {
		this.feetMountingResetBinding = feetMountingResetBinding;
	}

	public String getPauseTrackingBinding() {
		return pauseTrackingBinding;
	}

	public void setPauseTrackingBinding(String pauseTrackingBinding) {
		this.pauseTrackingBinding = pauseTrackingBinding;
	}

	public float getFullResetDelay() {
		return fullResetDelay;
	}

	public void setFullResetDelay(float delay) {
		fullResetDelay = delay;
	}

	public float getYawResetDelay() {
		return yawResetDelay;
	}

	public void setYawResetDelay(float delay) {
		yawResetDelay = delay;
	}

	public float getMountingResetDelay() {
		return mountingResetDelay;
	}

	public void setMountingResetDelay(float delay) {
		mountingResetDelay = delay;
	}

	public float getFeetMountingResetDelay() {
		return feetMountingResetDelay;
	}

	public void setFeetMountingResetDelay(float delay) {
		feetMountingResetDelay = delay;
	}

	public float getPauseTrackingDelay() {
		return pauseTrackingDelay;
	}

	public void setPauseTrackingDelay(float delay) {
		pauseTrackingDelay = delay;
	}
}
