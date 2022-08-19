package dev.slimevr.math;

import com.jme3.math.Quaternion;


public class QuaternionMovingAverage {
	private float amount;
	private int buffer;
	private CircularArrayList<Quaternion> quatBuffer;
	private CircularArrayList<Quaternion> rotBuffer;
	private Quaternion averageRotation;
	private Quaternion filteredQuaternion;

	public QuaternionMovingAverage(float amount, int buffer) {
		this.amount = amount;
		this.buffer = buffer;

		quatBuffer = new CircularArrayList<>(buffer);
		rotBuffer = new CircularArrayList<>(buffer);
	}

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
			quatBuffer.remove(0);
		}
		quatBuffer.add(q);

		if (quatBuffer.size() > 1) {
			if (rotBuffer.size() > buffer) {
				rotBuffer.remove(0);
			}
			rotBuffer
				.add(
					quatBuffer
						.get(quatBuffer.size() - 1)
						.mult(quatBuffer.get(quatBuffer.size() - 2))
				);
		}
	}

	public Quaternion getFilteredQuaternion() {
		return filteredQuaternion;
	}
}
