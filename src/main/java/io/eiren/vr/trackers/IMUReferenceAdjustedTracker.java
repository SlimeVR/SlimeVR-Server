package io.eiren.vr.trackers;

public class IMUReferenceAdjustedTracker<T extends IMUTracker & TrackerWithTPS & TrackerWithBattery> extends ReferenceAdjustedTracker<T> implements TrackerWithTPS, TrackerWithBattery {
	
	public IMUReferenceAdjustedTracker(T tracker) {
		super(tracker);
	}

	@Override
	public float getBatteryLevel() {
		return tracker.getBatteryLevel();
	}

	@Override
	public float getBatteryVoltage() {
		return tracker.getBatteryVoltage();
	}

	@Override
	public float getTPS() {
		return tracker.getTPS();
	}

	@Override
	public void dataTick() {
		tracker.dataTick();
	}
	
}
