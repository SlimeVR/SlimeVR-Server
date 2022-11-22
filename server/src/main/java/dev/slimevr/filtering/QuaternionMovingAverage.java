package dev.slimevr.filtering;

import com.jme3.math.Quaternion;
import com.jme3.system.NanoTimer;
import dev.slimevr.Main;


public class QuaternionMovingAverage {

	private final float smoothFactor;
	private final float predictFactor;
	private final CircularArrayList<Quaternion> rotBuffer;
	private final Quaternion quatBuf = new Quaternion();
	private final Quaternion latestQuaternion;
	private final Quaternion smoothingQuaternion;
	private final Quaternion filteredQuaternion;
	private final boolean smooths;
	private final boolean predicts;
	private final NanoTimer fpsTimer;
	private int smoothingCounter;

	// influences the range of smoothFactor.
	private static final float SMOOTH_MULTIPLIER = 42f;
	private static final float SMOOTH_MIN = 20f;

	// influences the range of predictFactor
	private static final float PREDICT_MULTIPLIER = 14f;
	private static final float PREDICT_MIN = 10f;

	// how many past rotations are used for prediction.
	private static final int PREDICT_BUFFER = 6;


	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		Quaternion initialRotation
	) {
		fpsTimer = Main.vrServer.getFpsTimer();

		// amount should range from 0 to 1.
		// GUI should clamp it from 0.01 (1%) or 0.1 (10%)
		// to 1 (100%).
		amount = Math.max(amount, 0);

		if (type == TrackerFilters.SMOOTHING) {
			smooths = true;
			// lower smoothFactor = more smoothing
			smoothFactor = SMOOTH_MULTIPLIER * (1 - amount) + SMOOTH_MIN;
		} else {
			smooths = false;
			smoothFactor = 0f;
		}

		if (type == TrackerFilters.PREDICTION) {
			predicts = true;
			// higher predictFactor = more prediction
			predictFactor = (PREDICT_MULTIPLIER * amount) + PREDICT_MIN;
			rotBuffer = new CircularArrayList<>(PREDICT_BUFFER);
		} else {
			predicts = false;
			predictFactor = 0;
			rotBuffer = null;
		}

		filteredQuaternion = new Quaternion(initialRotation);
		latestQuaternion = new Quaternion(initialRotation);
		smoothingQuaternion = new Quaternion(initialRotation);
	}

	// Runs at up to 1000hz. We use a timer to make it framerate-independent
	// since it runs between 850hz to 900hz in practice.
	synchronized public void update() {
		if (predicts) {
			if (rotBuffer.size() > 0) {
				quatBuf.set(latestQuaternion);

				// Applies the past rotations to the current rotation
				rotBuffer.forEach(quatBuf::multLocal);

				// Slerps the target rotation to that predicted rotation by
				// a certain factor.
				filteredQuaternion
					.slerpLocal(quatBuf, predictFactor * fpsTimer.getTimePerFrame());
			}
		}

		if (smooths) {
			// Calculate the slerp factor and limit it to 1 max
			smoothingCounter++;
			float amt = Math.min(smoothFactor * fpsTimer.getTimePerFrame() * smoothingCounter, 1);

			// Smooth towards the target rotation
			filteredQuaternion
				.slerp(
					smoothingQuaternion,
					latestQuaternion,
					amt
				);
		}
	}

	synchronized public void addQuaternion(Quaternion q) {
		if (predicts) {
			if (rotBuffer.size() == rotBuffer.capacity()) {
				rotBuffer.removeLast();
			}

			// Gets and stores the rotation between the last 2 quaternions
			quatBuf.set(latestQuaternion);
			quatBuf.inverseLocal();
			rotBuffer.add(quatBuf.mult(q));
		}

		if (smooths) {
			smoothingCounter = 0;
			smoothingQuaternion.set(filteredQuaternion);
		}

		latestQuaternion.set(q);
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion.clone();
	}
}
