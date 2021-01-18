package io.eiren.vr.trackers;

public enum TrackerStatus {
	
	DISCONNECTED(false),
	OK(true),
	BUSY(true),
	ERROR(false),
	;
	
	public final boolean sendData;
	
	private TrackerStatus(boolean sendData) {
		this.sendData = sendData;
	}
}
