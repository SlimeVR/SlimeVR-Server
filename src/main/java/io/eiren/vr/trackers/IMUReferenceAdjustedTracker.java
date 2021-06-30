package io.eiren.vr.trackers;

public class IMUReferenceAdjustedTracker<T extends IMUTracker & TrackerWithTPS & TrackerWithBattery> extends ReferenceAdjustedTracker implements TrackerWithTPS, TrackerWithBattery {
	
	public IMUReferenceAdjustedTracker(T tracker) {
		super(tracker);
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getBatteryLevel() {
		return ((T) tracker).getBatteryLevel();
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getBatteryVoltage() {
		return ((T) tracker).getBatteryVoltage();
	}

	@SuppressWarnings("unchecked")
	@Override
	public float getTPS() {
		return ((T) tracker).getTPS();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dataTick() {
		((T) tracker).dataTick();
	}
	
}
