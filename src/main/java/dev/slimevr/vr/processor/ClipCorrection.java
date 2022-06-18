package dev.slimevr.vr.processor;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class ClipCorrection {
	// class vars
	private float floorLevel;
	private float maxDynamicDisplacement = 0.04f;
	private float dynamicDisplacementCutoff = 0.75f;
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

	private Vector3f leftWaistUpperLegOffset = new Vector3f();
	private Vector3f rightWaistUpperLegOffset = new Vector3f();

	public ClipCorrection(float floorLevel) {
		this.floorLevel = floorLevel;
	}

	// update the offsets for the waist and upper leg
	public void updateOffsets(Vector3f upperLeftLeg, Vector3f upperRightLeg, Vector3f waist) {
		// update the relevant leg data
		this.leftWaistUpperLegOffset = upperLeftLeg.subtract(waist);
		this.rightWaistUpperLegOffset = upperRightLeg.subtract(waist);
	}

	public Vector3f getLeftFootPosition() {
		return leftFootPosition;
	}

	public void setLeftFootPosition(Vector3f leftFootPosition) {
		this.leftFootPosition = leftFootPosition.clone();
	}

	public Vector3f getRightFootPosition() {
		return rightFootPosition;
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public Vector3f getLeftKneePosition() {
		return leftKneePosition;
	}

	public void setLeftKneePosition(Vector3f leftKneePosition) {
		this.leftKneePosition = leftKneePosition.clone();
	}

	public Vector3f getRightKneePosition() {
		return rightKneePosition;
	}

	public void setRightKneePosition(Vector3f rightKneePosition) {
		this.rightKneePosition = rightKneePosition.clone();
	}

	public Vector3f getWaistPosition() {
		return waistPosition;
	}

	public void setWaistPosition(Vector3f waistPosition) {
		this.waistPosition = waistPosition.clone();
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
			floorLevel = (leftFootPosition.y + rightFootPosition.y) / 2f;
			initialized = true;
		}
		// calculate how angled down the feet are as a scalar value between 0
		// and 1 (0 = flat, 1 = max angle)
		float leftOffset = getLeftFootOffset();
		float rightOffset = getRightFootOffset();

		// if there is no clipping, or clipping is not enabled, return false
		if (!isClipped(leftOffset, rightOffset) || !enabled) {
			return false;
		}

		Vector3f leftWaist = waistPosition.add(leftWaistUpperLegOffset);
		Vector3f rightWaist = waistPosition.add(rightWaistUpperLegOffset);

		float leftKneeWaist = leftKneePosition.distance(leftWaist);
		float rightKneeWaist = rightKneePosition.distance(rightWaist);

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
		float leftKneeWaistNew = leftKneePosition.distance(leftWaist);
		float rightKneeWaistNew = rightKneePosition.distance(rightWaist);
		float leftKneeOffset = leftKneeWaistNew - leftKneeWaist;
		float rightKneeOffset = rightKneeWaistNew - rightKneeWaist;

		// get the vector from the waist to the knee
		Vector3f leftKneeVector = leftKneePosition
			.subtract(leftWaist)
			.normalize()
			.mult(leftKneeOffset);
		Vector3f rightKneeVector = rightKneePosition
			.subtract(rightWaist)
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
		} else if (offset < -dynamicDisplacementCutoff) {
			return dynamicDisplacementCutoff;
		}
		return offset * -1;
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		if (offset > 0) {
			return 0;
		} else if (offset < -dynamicDisplacementCutoff) {
			return dynamicDisplacementCutoff;
		}
		return offset * -1;
	}

	// get the unit vector of the given rotation
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.mult(normal).normalize();
	}
}
