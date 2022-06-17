package dev.slimevr.vr.processor;


import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class ClipCorrection {
	// class vars
	private float floorLevel;
	private float maxDynamicDisplacement = 0.04f;
	private boolean initialized = true;
	private boolean enabled = true;
	private final Vector3f normal = new Vector3f(0, 0, -1);

	// variables for holding relavant leg data
	private Vector3f leftFootPosition = new Vector3f();
	private Vector3f rightFootPosition = new Vector3f();
	private Vector3f leftKneePosition = new Vector3f();
	private Vector3f rightKneePosition = new Vector3f();
	private Vector3f waistPosition = new Vector3f();
	private Quaternion leftFootRotation = new Quaternion();
	private Quaternion rightFootRotation = new Quaternion();

	public ClipCorrection(float floorLevel) {
		this.floorLevel = floorLevel;
	}

	public void update(
		Vector3f leftFootPosition,
		Vector3f rightFootPosition,
		Vector3f leftKneePosition,
		Vector3f rightKneePosition,
		Vector3f waistPosition,
		Quaternion leftFootRotation,
		Quaternion rightFootRotation
	) {
		// update the relevant leg data
		this.leftFootPosition = leftFootPosition.clone();
		this.rightFootPosition = rightFootPosition.clone();
		this.leftKneePosition = leftKneePosition.clone();
		this.rightKneePosition = rightKneePosition.clone();
		this.waistPosition = waistPosition.clone();
		this.leftFootRotation = leftFootRotation.clone();
		this.rightFootRotation = rightFootRotation.clone();
	}

	public Vector3f getLeftFootPosition() {
		return leftFootPosition;
	}

	public Vector3f getRightFootPosition() {
		return rightFootPosition;
	}

	public Vector3f getLeftKneePosition() {
		return leftKneePosition;
	}

	public Vector3f getRightKneePosition() {
		return rightKneePosition;
	}


	public double getFloorLevel() {
		return floorLevel;
	}

	public void setFloorLevel(float floorLevel) {
		this.floorLevel = floorLevel;
	}

	public void resetFloorLevel() {
		this.initialized = false;
	}

	// returns true if the foot is clipped and false if it is not
	public boolean isClipped(float leftOffset, float rightOffset) {
		return (leftFootPosition.y < floorLevel + leftOffset
			|| rightFootPosition.y < floorLevel + rightOffset);
	}

	// returns true if the tracker positions should be corrected with the values
	// stored in the class
	public boolean correctClipping() {
		// if not initialized, we need to initialize the floor level
		if (!initialized) {
			floorLevel = (leftFootPosition.y + rightFootPosition.y) / 2f + 0.025f;
			initialized = true;
		}
		// calculate how angled down the feet are as a scalar value between 0
		// and 1 (0 = flat, 1 = max angle)
		float leftOffset = getLeftFootOffset();
		float rightOffset = getRightFootOffset();

		// if there is no clipping, return false
		if (!isClipped(leftOffset, rightOffset)) {
			return false;
		}

		// this clipping correction assumes that the waist and the knees
		// are always suposed to be the same distance apart but this is not true
		// so this should be fixed
		float leftKneeWaist = leftKneePosition.distance(waistPosition);
		float rightKneeWaist = rightKneePosition.distance(waistPosition);

		// move the feet to their new positions and push the knees up
		if (leftFootPosition.y < floorLevel + (maxDynamicDisplacement * leftOffset)) {
			float displacement = floorLevel
				+ (maxDynamicDisplacement * leftOffset)
				- leftFootPosition.y;
			leftFootPosition.y += displacement;
			leftKneePosition.y += displacement;
		}
		if (rightFootPosition.y < floorLevel + (maxDynamicDisplacement * rightOffset)) {
			float displacement = floorLevel
				+ (maxDynamicDisplacement * rightOffset)
				- rightFootPosition.y;
			rightFootPosition.y += displacement;
			rightKneePosition.y += displacement;
		}

		// calculate the correction for the knees
		float leftKneeWaistNew = leftKneePosition.distance(waistPosition);
		float rightKneeWaistNew = rightKneePosition.distance(waistPosition);
		float leftKneeOffset = leftKneeWaistNew - leftKneeWaist;
		float rightKneeOffset = rightKneeWaistNew - rightKneeWaist;

		// get the vector from the waist to the knee
		Vector3f leftKneeVector = leftKneePosition
			.subtract(waistPosition)
			.normalize()
			.mult(leftKneeOffset);
		Vector3f rightKneeVector = rightKneePosition
			.subtract(waistPosition)
			.normalize()
			.mult(rightKneeOffset);

		// correct the knees (might not be mathmatically correct but it is
		// close)
		leftKneePosition = leftKneePosition.subtract(leftKneeVector);
		rightKneePosition = rightKneePosition.subtract(rightKneeVector);

		return true;
	}

	private float getLeftFootOffset() {
		float offset = computeUnitVector(this.leftFootRotation).y;
		if (offset > 0) {
			return 0;
		}
		return offset * -1;
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		if (offset > 0) {
			return 0;
		}
		return offset * -1;
	}

	// get the z component of the unit vector of the given quaternion
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.mult(normal).normalize();
	}
}
