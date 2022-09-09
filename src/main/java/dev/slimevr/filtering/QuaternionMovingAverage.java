package dev.slimevr.filtering;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float smoothFactor;
	private final float predictFactor;
	private final CircularArrayList<Quaternion> rotBuffer;
	private final Quaternion quatBuf = new Quaternion();
	private final Quaternion targetQuat = new Quaternion();
	private final Quaternion lastQuaternion;
	private final Quaternion filteredQuaternion;
	private static final float SMOOTH_QUADRATIC_DIVIDER = 12.6f;
	private static final float SMOOTH_QUADRATIC_MIN = 0.02f;
	private static final float PREDICT_QUADRATIC_DIVIDER = 3.2f;
	private static final float PREDICT_QUADRATIC_MIN = 0.1f;
	private static final int PREDICT_BUFFER = 6;


	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		Quaternion initialRotation
	) {
		amount = Math.max(amount, 0);

		if (type == TrackerFilters.SMOOTHING) {
			smoothFactor = FastMath.pow(1 - amount, 2) / SMOOTH_QUADRATIC_DIVIDER
				+ SMOOTH_QUADRATIC_MIN;
		} else {
			// smooths a little to reduce jitter
			smoothFactor = 0.08f - (amount / 100f);
		}

		if (type == TrackerFilters.PREDICTION) {
			rotBuffer = new CircularArrayList<>(PREDICT_BUFFER);
			predictFactor = FastMath.pow(amount, 2) / PREDICT_QUADRATIC_DIVIDER
				+ PREDICT_QUADRATIC_MIN;
		} else {
			rotBuffer = null;
			predictFactor = 0;
		}

		filteredQuaternion = new Quaternion(initialRotation);
		lastQuaternion = new Quaternion(initialRotation);
	}

	// 1000hz
	synchronized public void update() {
		targetQuat.set(lastQuaternion);

		// Prediction
		if (rotBuffer != null) {
			if (rotBuffer.size() > 0) {
				quatBuf.set(targetQuat);
				rotBuffer.forEach(quatBuf::multLocal);
				targetQuat.slerpLocal(quatBuf, predictFactor);
			}
		}

		// Smoothing
		filteredQuaternion.slerpLocal(targetQuat, smoothFactor);
	}

	synchronized public void addQuaternion(Quaternion q) {
		if (rotBuffer != null) {
			if (rotBuffer.size() == rotBuffer.capacity()) {
				rotBuffer.remove(0);
			}
			quatBuf.set(lastQuaternion);
			quatBuf.inverseLocal();
			rotBuffer.add(quatBuf.mult(q));
		}

		lastQuaternion.set(q);
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion.clone();
	}
}
