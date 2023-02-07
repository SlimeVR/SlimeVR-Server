package dev.slimevr.config;


public class VMCConfig extends OSCConfig {

	// Anchor the tracking at the hip?
	boolean anchorHip = true;
	// Address of the VRM to be used
	String VRMAddress = "";

	public VMCConfig() {
	}

	public boolean getAnchorHip() {
		return anchorHip;
	}

	public void setAnchorHip(boolean anchorHip) {
		this.anchorHip = anchorHip;
	}

	public String getVRMAddress() {
		return VRMAddress;
	}

	public void setVRMAddress(String VRMAddress) {
		this.VRMAddress = VRMAddress;
	}
}
