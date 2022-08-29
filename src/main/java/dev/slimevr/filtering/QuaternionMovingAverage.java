package dev.slimevr.filtering;

import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {

	private final float factor;
	private CircularArrayList<Quaternion> quatBuffer;
	private CircularArrayList<Quaternion> rotBuffer;
	private Quaternion averageRotation;
	private final Quaternion filteredQuaternion;
	private static final float SMOOTH_MULTIPLIER = 0.95f;
	private static final float PREDICT_MULTIPLIER = 2.0f;

	public QuaternionMovingAverage(
		TrackerFilters type,
		float amount,
		int buffer,
		Quaternion initialRotation
	) {
		buffer = Math.max(buffer, 1);
		int latency = buffer * 2;

		if (type == TrackerFilters.SMOOTHING) {
			this.factor = (1 - (amount / SMOOTH_MULTIPLIER)) / latency;
		} else { // if prediction
			this.factor = (amount / PREDICT_MULTIPLIER / latency) + 1;
		}

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);

		filteredQuaternion = new Quaternion(initialRotation);
		quatBuffer.add(initialRotation);
	}
	// TODO Buffer? Latency? Protocol...

	// 1000hz
	synchronized public void update() {
		if (quatBuffer.size() > 0) {
			filteredQuaternion
				.slerpLocal(quatBuffer.get(quatBuffer.size() - 1), factor);
		}
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
