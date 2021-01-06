package io.eiren.vr.bridge;

public interface VRBridge {
	
	public VRBridgeState getBridgeState();
	
	public static enum VRBridgeState {
		NOT_STARTED,
		STARTED,
		CONNECTED,
		ERROR
	}
}
