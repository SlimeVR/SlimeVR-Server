package dev.slimevr.filtering;

import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float factor;
	private final int buffer;
	private CircularArrayList<Quaternion> quatBuffer;
	private CircularArrayList<Quaternion> rotBuffer;
	private Quaternion averageRotation;
	private final Quaternion filteredQuaternion;
	private static final float SMOOTH_DIVIDER = 1.25f;
	private static final float PREDICT_DIVIDER = 1.25f;

	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		int buffer,
		Quaternion initialRotation
	) {
		// Dividing to control range.
		if (type == TrackerFilters.SMOOTHING) {
			this.factor = 1 - (amount / SMOOTH_DIVIDER);
		} else { // if prediction
			this.factor = 1 + (amount / PREDICT_DIVIDER);
		}

		// Sanity check to prevent crash.
		this.buffer = Math.max(buffer, 1);

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);

		filteredQuaternion = new Quaternion(initialRotation);
		quatBuffer.add(initialRotation);
	}

	// 1000hz
	synchronized public void update() {
		if (quatBuffer.size() > 0) {
			filteredQuaternion
				.slerp(filteredQuaternion, quatBuffer.get(quatBuffer.size() - 1), factor);
		}
		// TODO
	}

	synchronized public void addQuaternion(Quaternion q) {
		if (quatBuffer.size() == buffer) {
			quatBuffer.remove(0);
		}
		quatBuffer.add(q);
		// TODO
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion;
	}
}
