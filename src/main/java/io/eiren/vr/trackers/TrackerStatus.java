package io.eiren.vr.trackers;

public enum TrackerStatus {
	
	DISCONNECTED(0, false),
	OK(1, true),
	BUSY(2, true),
	ERROR(3, false),
	OCCLUDED(4, false),
	;
	
	private static final TrackerStatus byId[] = new TrackerStatus[5];
	
	public final int id;
	public final boolean sendData;
	
	private TrackerStatus(int id, boolean sendData) {
		this.sendData = sendData;
		this.id = id;
	}
	
	public static TrackerStatus getById(int id) {
		if(id < 0 || id >= byId.length)
			return null;
		return byId[id];
	}
	
	static {
		for(TrackerStatus st : values())
			byId[st.id] = st;
	}
}
