package dev.slimevr.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;

import java.util.HashMap;
import java.util.Map;

public class KeybindingsConfig {

	private Keybind keybind;

	private String fullResetBinding = "CTRL+ALT+SHIFT+Y";

	private String yawResetBinding = "CTRL+ALT+SHIFT+U";

	private String mountingResetBinding = "CTRL+ALT+SHIFT+I";

	private String pauseTrackingBinding = "CTRL+ALT+SHIFT+O";

	private long fullResetDelay = 0L;

	private long yawResetDelay = 0L;

	private long mountingResetDelay = 0L;

	private long pauseTrackingDelay = 0L;

	public KeybindingsConfig() {}

	/*
	public Int getKeybindId() {
		return keybind;
	}
	 */

	public String getKeybindValue() {
		return switch (keybind) {
			case FullResetBinding -> fullResetBinding;
			case YawResetBinding -> yawResetBinding;
			case MountingResetBinding -> mountingResetBinding;
			case PauseTrackingBinding -> pauseTrackingBinding;
			default -> "";
		};
	}

	public Long getDelay() {
		return switch (keybind) {
			case FullResetBinding -> fullResetDelay;
			case YawResetBinding -> yawResetDelay;
			case MountingResetBinding -> mountingResetDelay;
			case PauseTrackingBinding -> pauseTrackingDelay;
			default -> 0L;
		};
	}

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

	public String getPauseTrackingBinding() {
		return pauseTrackingBinding;
	}

	public void setPauseTrackingBinding(String pauseTrackingBinding) {
		this.pauseTrackingBinding = pauseTrackingBinding;
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

	public enum Keybind {
		FullResetBinding,
		YawResetBinding,
		MountingResetBinding,
		PauseTrackingBinding
	}
}
