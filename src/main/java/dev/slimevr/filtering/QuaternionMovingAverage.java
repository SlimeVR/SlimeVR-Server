package dev.slimevr.filtering;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float smoothFactor;
	private final float predictFactor;
	private final boolean smooths;
	private final boolean predicts;
	private final CircularArrayList<Quaternion> rotBuffer;
	private final Quaternion quatBuf = new Quaternion();
	private final Quaternion targetQuat = new Quaternion();
	private final Quaternion lastQuaternion;
	private final Quaternion filteredQuaternion;
	private static final float SMOOTH_QUADRATIC_DIVIDER = 16f;
	private static final float SMOOTH_QUADRATIC_MIN = 0.015f;
	private static final float PREDICT_QUADRATIC_DIVIDER = 4f;
	private static final float PREDICT_QUADRATIC_MIN = 0.06f;
	private static final int PREDICT_BUFFER = 6;


	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		Quaternion initialRotation
	) {
		smooths = type == TrackerFilters.SMOOTHING;
		predicts = type == TrackerFilters.PREDICTION;

		amount = Math.max(amount, 0);
		smoothFactor = FastMath.pow(1 - amount, 2) / SMOOTH_QUADRATIC_DIVIDER
			+ SMOOTH_QUADRATIC_MIN;
		predictFactor = FastMath.pow(amount, 2) / PREDICT_QUADRATIC_DIVIDER
			+ PREDICT_QUADRATIC_MIN;

		if (predicts) {
			rotBuffer = new CircularArrayList<>(PREDICT_BUFFER);
		} else {
			rotBuffer = null;
		}

		filteredQuaternion = new Quaternion(initialRotation);
		lastQuaternion = new Quaternion(initialRotation);
	}

	// 1000hz
	synchronized public void update() {
		targetQuat.set(lastQuaternion);

		if (predicts) {
			if (rotBuffer.size() > 0) {
				quatBuf.set(targetQuat);
				rotBuffer.forEach(quatBuf::multLocal);
				targetQuat.slerpLocal(quatBuf, predictFactor);
			}
		}

		if (smooths) {
			filteredQuaternion.slerpLocal(targetQuat, smoothFactor);
		} else {
			filteredQuaternion.set(targetQuat);
		}
	}

	int i;

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
