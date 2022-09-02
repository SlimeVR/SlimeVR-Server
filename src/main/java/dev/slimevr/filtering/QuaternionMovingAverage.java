package dev.slimevr.filtering;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float smoothFactor, predictFactor;
	private final TrackerFilters type;
	private final CircularArrayList<Quaternion> quatBuffer;
	private final CircularArrayList<Quaternion> rotBuffer;
	private final Quaternion filteredQuaternion;
	private static final float SMOOTH_QUADRATIC_DIVIDER = 18f;
	private static final float SMOOTH_QUADRATIC_MIN = 0.013f;

	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		Quaternion initialRotation
	) {
		this.type = type;

		amount = Math.max(amount, 0);
		smoothFactor = (FastMath.pow(1 - amount, 2) / SMOOTH_EXPONENTIAL_DIVIDER)
			+ SMOOTH_EXPONENTIAL_MIN;
		predictFactor = 1 + amount;

		int buffer = 3;

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);

		filteredQuaternion = new Quaternion(initialRotation);
		quatBuffer.add(initialRotation);
	}

	// 1000hz
	synchronized public void update() {
		if (type == TrackerFilters.SMOOTHING) {
			if (quatBuffer.size() > 0) {
				filteredQuaternion
					.slerpLocal(
						quatBuffer.get(quatBuffer.size() - 1),
						smoothFactor
					);
			}
		} else if (type == TrackerFilters.PREDICTION) {
			if (quatBuffer.size() > 1) {
				filteredQuaternion
					.slerpLocal(quatBuffer.get(quatBuffer.size() - 1), predictFactor);
			}
		}
		// TODO: prediction
	}

	synchronized public void addQuaternion(Quaternion q) {
		if (quatBuffer.size() == quatBuffer.capacity()) {
			quatBuffer.remove(0);
		}
		quatBuffer.add(q);
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion.clone();
	}
}
