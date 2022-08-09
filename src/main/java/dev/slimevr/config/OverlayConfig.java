package dev.slimevr.config;

public class OverlayConfig {

	private boolean isMirrored = false;
	private boolean isVisible = false;


	public boolean isMirrored() {
		return isMirrored;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setMirrored(boolean mirrored) {
		isMirrored = mirrored;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}
}
