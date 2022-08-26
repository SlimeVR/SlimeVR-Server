package dev.slimevr.filtering;

import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float factor;
	private final int buffer;
	private CircularArrayList<Quaternion> quatBuffer;
	private CircularArrayList<Quaternion> rotBuffer;
	private Quaternion averageRotation;
	private final Quaternion filteredQuaternion;

	/**
	 * @param factor <1 = smoothing. >1 = prediction.
	 * @param buffer How many quaternions are kept for the average.
	 */
	public QuaternionMovingAverage(float factor, int buffer, Quaternion initialRotation) {
		// Sanity checks. GUI should clamp the values.
		if (factor < 0)
			factor = 0;
		if (buffer < 1)
			buffer = 1;

		this.factor = factor;
		this.buffer = buffer;

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);

		filteredQuaternion = new Quaternion(initialRotation);
		quatBuffer.add(initialRotation);
	}

	// 1000hz
	synchronized public void update() {
		if (quatBuffer.size() > 0) {
			filteredQuaternion
				.slerp(quatBuffer.get(0), quatBuffer.get(quatBuffer.size() - 1), factor);
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
