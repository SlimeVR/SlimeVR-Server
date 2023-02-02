package dev.slimevr.config;


public class VMCConfig extends OSCConfig {

	// Anchor the tracking at the hip?
	boolean anchorHip = true;

	public VMCConfig() {
	}

	public boolean getAnchorHip() {
		return anchorHip;
	}

	public void setAnchorHip(boolean anchorHip) {
		this.anchorHip = anchorHip;
	}
}
