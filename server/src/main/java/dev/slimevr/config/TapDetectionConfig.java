package dev.slimevr.config;

import com.jme3.math.FastMath;


// handles the tap detection config
// this involves the number of taps, the delay, and whether or not the feature is enabled
// for each reset type
public class TapDetectionConfig {

	private float yawResetDelay = 0.2f;
	private float fullResetDelay = 1.0f;
	private float mountingResetDelay = 1.0f;
	private boolean yawResetEnabled = true;
	private boolean fullResetEnabled = true;
	private boolean mountingResetEnabled = true;
	private int yawResetTaps = 2;
	private int fullResetTaps = 3;
	private int mountingResetTaps = 3;
	private int numberTrackersOverThreshold = 1;

	public float getYawResetDelay() {
		return yawResetDelay;
	}

	public void setYawResetDelay(float yawResetDelay) {
		this.yawResetDelay = yawResetDelay;
	}

	public float getFullResetDelay() {
		return fullResetDelay;
	}

	public void setFullResetDelay(float fullResetDelay) {
		this.fullResetDelay = fullResetDelay;
	}

	public float getMountingResetDelay() {
		return mountingResetDelay;
	}

	public void setMountingResetDelay(float mountingResetDelay) {
		this.mountingResetDelay = mountingResetDelay;
	}

	public boolean getYawResetEnabled() {
		return yawResetEnabled;
	}

	public void setYawResetEnabled(boolean yawResetEnabled) {
		this.yawResetEnabled = yawResetEnabled;
	}

	public boolean getFullResetEnabled() {
		return fullResetEnabled;
	}

	public void setFullResetEnabled(boolean fullResetEnabled) {
		this.fullResetEnabled = fullResetEnabled;
	}

	public boolean getMountingResetEnabled() {
		return mountingResetEnabled;
	}

	public void setMountingResetEnabled(boolean mountingResetEnabled) {
		this.mountingResetEnabled = mountingResetEnabled;
	}

	public int getYawResetTaps() {
		return yawResetTaps;
	}

	// clamp to 2-3 to prevent errors
	public void setYawResetTaps(int yawResetTaps) {
		this.yawResetTaps = (int) FastMath.clamp(yawResetTaps, 2, 10);
		this.yawResetTaps = yawResetTaps;
	}

	public int getFullResetTaps() {
		return fullResetTaps;
	}

	public void setFullResetTaps(int fullResetTaps) {
		this.fullResetTaps = (int) FastMath.clamp(fullResetTaps, 2, 10);
		this.fullResetTaps = fullResetTaps;
	}

	public int getMountingResetTaps() {
		return mountingResetTaps;
	}

	public void setMountingResetTaps(int mountingResetTaps) {
		this.mountingResetTaps = (int) FastMath.clamp(mountingResetTaps, 2, 10);
		this.mountingResetTaps = mountingResetTaps;
	}

	public int getNumberTrackersOverThreshold() {
		return numberTrackersOverThreshold;
	}

	public void setNumberTrackersOverThreshold(int numberTrackersOverThreshold) {
		this.numberTrackersOverThreshold = numberTrackersOverThreshold;
	}

}
