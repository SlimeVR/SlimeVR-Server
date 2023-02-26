package dev.slimevr.config;

import com.jme3.math.FastMath;


// handles the tap detection config
// this involves the number of taps, the delay, and whether or not the feature is enabled
// for each reset type
public class TapDetectionConfig {

	private float quickResetDelay = 0.2f;
	private float resetDelay = 1.0f;
	private float mountingResetDelay = 1.0f;
	private boolean quickResetEnabled = true;
	private boolean resetEnabled = true;
	private boolean mountingResetEnabled = true;
	private int quickResetTaps = 2;
	private int resetTaps = 3;
	private int mountingResetTaps = 3;
	private int numberTrackersOverThreshold = 1;

	public float getQuickResetDelay() {
		return quickResetDelay;
	}

	public void setQuickResetDelay(float quickResetDelay) {
		this.quickResetDelay = quickResetDelay;
	}

	public float getResetDelay() {
		return resetDelay;
	}

	public void setResetDelay(float resetDelay) {
		this.resetDelay = resetDelay;
	}

	public float getMountingResetDelay() {
		return mountingResetDelay;
	}

	public void setMountingResetDelay(float mountingResetDelay) {
		this.mountingResetDelay = mountingResetDelay;
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

	public int getQuickResetTaps() {
		return quickResetTaps;
	}

	// clamp to 2-3 to prevent errors
	public void setQuickResetTaps(int quickResetTaps) {
		this.quickResetTaps = (int) FastMath.clamp(quickResetTaps, 2, 10);
		this.quickResetTaps = quickResetTaps;
	}

	public int getResetTaps() {
		return resetTaps;
	}

	public void setResetTaps(int resetTaps) {
		this.resetTaps = (int) FastMath.clamp(resetTaps, 2, 10);
		this.resetTaps = resetTaps;
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
