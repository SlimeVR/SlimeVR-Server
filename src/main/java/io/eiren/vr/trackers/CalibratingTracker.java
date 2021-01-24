package io.eiren.vr.trackers;

import java.util.function.Consumer;

public interface CalibratingTracker {
	
	public void startCalibration(Consumer<String> calibrationDataConsumer);
	
	public void requestCalibrationData(Consumer<String> calibrationDataConsumer);
	
	public void uploadNewClibrationData();
}
