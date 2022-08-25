package dev.slimevr.filtering;

import com.jme3.math.Quaternion;
import io.eiren.util.logging.LogManager;


public class QuaternionMovingAverage {

	private float factor;
	private int buffer;
	private CircularArrayList<Quaternion> quatBuffer;
	private CircularArrayList<Quaternion> rotBuffer;
	private Quaternion averageRotation;
	private Quaternion filteredQuaternion;

	/**
	 * @param factor <1 = smoothing. >1 = prediction.
	 * @param buffer How many quaternions are kept for the average.
	 */
	public QuaternionMovingAverage(float factor, int buffer) {
		// Sanity checks. GUI should clamp the values.
		if (factor < 0)
			factor = 0;
		if (buffer < 1)
			buffer = 1;

		this.factor = factor;
		this.buffer = buffer;

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);
	}

	// 1000hz
	public void update() {
		averageRotation = new Quaternion();
		filteredQuaternion = new Quaternion();
	}
	// TODO update
	// TODO synchronized
	// TODO math
	// TODO average

	public void addQuaternion(Quaternion q) {
		if (quatBuffer.size() == buffer) {
			quatBuffer.remove(0);
		}
		quatBuffer.add(q);
		LogManager.log.debug(String.valueOf(quatBuffer.size()));
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion;
	}
}
