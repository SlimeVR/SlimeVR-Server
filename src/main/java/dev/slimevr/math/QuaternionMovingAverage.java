package dev.slimevr.math;

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
		LogManager.log.debug("createddddddddddddddddddddddd");
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
	// TODO ^
	// TODO synchronized
	// TODO math
	// TODO average
	// TODO circulararraylist stuff

	public void addQuaternion(Quaternion q) {
		if (quatBuffer.size() > buffer) {
			quatBuffer.remove(quatBuffer.size() + 1);
		}
		quatBuffer.add(q);
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion;
	}
}
