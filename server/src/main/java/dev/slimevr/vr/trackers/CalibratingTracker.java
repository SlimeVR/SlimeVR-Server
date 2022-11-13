package dev.slimevr.vr.trackers;

import java.util.function.Consumer;


public interface CalibratingTracker {

	void startCalibration(Consumer<String> calibrationDataConsumer);

	void requestCalibrationData(Consumer<String> calibrationDataConsumer);

	void uploadNewClibrationData();
}
