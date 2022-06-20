package dev.slimevr.vr.processor;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.processor.skeleton.LegTweakBuffer;
import com.jme3.math.FastMath;


public class ClipCorrection {
	// class vars
	private float floorLevel;
	private float waistToFloorDist;
	private float standingCuttoffhorizontal = 0.5f;
	private float standingCuttoffvertical = 0.35f;
	private float maxDynamicDisplacement = 0.04f;
	private float dynamicDisplacementCutoff = 0.7f;
	private boolean initialized = true;
	private boolean enabled = true;
	private boolean windingUp = true;
	private boolean windingDown = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;
	static final Quaternion FORWARD_QUATERNION = new Quaternion()
		.fromAngles(FastMath.HALF_PI, 0, 0);

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

	// buffer for holding previus frames of data
	private LegTweakBuffer legBufferHead = new LegTweakBuffer();

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

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation.clone();
	}

	public Vector3f getRightFootPosition() {
		return rightFootPosition;
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation.clone();
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

	public void enableFloorCLip() {
		this.enabled = true;
	}

	public void disableFloorCLip() {
		this.enabled = false;
	}

	// tweak the position of the legs based on data from the last frames
	public boolean tweakLegs() {
		// if not initialized, we need to initialize floor level and waist to
		// floor distance (must happen immediately after reset)
		if (!initialized) {
			floorLevel = (leftFootPosition.y + rightFootPosition.y) / 2f + 0.02f;
			waistToFloorDist = waistPosition.y - floorLevel;
			initialized = true;
		}
		// if not enabled do nothing and return false
		if (!enabled) {
			return false;
		}
		// if the user does not have the majority of their weight on their feet
		// start checking for a good time to disable the floor clip without
		// causing skipping
		if (!isStanding()) {
			windingDown = true;
			windingUp = false;

			// if we are winding down and both legs are not active just return
			// false
			if (!rightLegActive && !leftLegActive) {
				return false;
			}
		}
		// if the user has the majority of their weight on their feet
		// start checking for a good time to enable the floor clip without
		// causing skipping
		else {
			windingDown = false;
			windingUp = true;
		}

		// first populate the buffer with the current data
		LegTweakBuffer currentFrame = new LegTweakBuffer(
			this.leftFootPosition,
			this.rightFootPosition,
			this.leftKneePosition,
			this.rightKneePosition,
			this.waistPosition,
			this.leftFootRotation,
			this.rightFootRotation,
			this.legBufferHead
		);
		this.legBufferHead = currentFrame;

		// now correct the position of the legs from the last frames data
		// not implemented yet...

		// once done run the clip correction (also corrects the position of the
		// knees)
		boolean corrected = correctClipping();

		// determine if either leg is in a position to activate or deactivate
		// (use the buffer to get the positions before corrections)
		float leftFootDif = legBufferHead.leftFootPosition.subtract(leftFootPosition).length();
		float rightFootDif = legBufferHead.rightFootPosition.subtract(rightFootPosition).length();
		if (windingDown && leftFootDif < 0.005f) {
			leftLegActive = false;
		} else if (windingUp && leftFootDif < 0.005f) {
			leftLegActive = true;
		}
		if (windingDown && rightFootDif < 0.005f) {
			rightLegActive = false;
		} else if (windingUp && rightFootDif < 0.005f) {
			rightLegActive = true;
		}

		// restore the positions of inactive legs
		if (!leftLegActive) {
			leftFootPosition = legBufferHead.leftFootPosition.clone();
			leftKneePosition = legBufferHead.leftKneePosition.clone();
		}
		if (!rightLegActive) {
			rightFootPosition = legBufferHead.rightFootPosition.clone();
			rightKneePosition = legBufferHead.rightKneePosition.clone();
		}

		// populate the corrected data into the current frame
		this.legBufferHead
			.populateCorrectedPositions(
				leftFootPosition,
				rightFootPosition,
				leftKneePosition,
				rightKneePosition,
				waistPosition,
				leftFootRotation,
				rightFootRotation
			);

		return corrected;
	}

	// returns true if the foot is clipped and false if it is not
	public boolean isClipped(float leftOffset, float rightOffset) {
		return (leftFootPosition.y < floorLevel + leftOffset
			|| rightFootPosition.y < floorLevel + rightOffset);
	}

	// returns true if the tracker positions should be corrected with the values
	// stored in the class
	public boolean correctClipping() {
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
			float displacement = Math
				.abs(
					floorLevel
						+ (maxDynamicDisplacement * leftOffset)
						- leftFootPosition.y
				);
			leftFootPosition.y += displacement;
			leftKneePosition.y += displacement;
		}
		if (rightFootPosition.y < floorLevel + (maxDynamicDisplacement * rightOffset)) {
			float displacement = Math
				.abs(
					floorLevel
						+ (maxDynamicDisplacement * rightOffset)
						- rightFootPosition.y
				);
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

	// returns true if it is likly the user is standing
	public boolean isStanding() {
		// if the waist is below the verticalcutoff, we are not standing
		if (
			waistPosition.y
				< floorLevel + waistToFloorDist - (waistToFloorDist * standingCuttoffvertical)
		) {
			return false;
		}
		// if the waist is above the verticalcutoff, we are standing as long as
		// the horizontal cutoff is not exceeded on both feet
		Vector3f left = leftFootPosition.clone();
		Vector3f right = leftFootPosition.clone();
		Vector3f waist = waistPosition.clone();
		left.y = 0;
		right.y = 0;
		waist.y = 0;
		return !(waist.distance(left) > standingCuttoffhorizontal
			&& waist.distance(right) > standingCuttoffhorizontal);
	}

	private float getLeftFootOffset() {
		float offset = computeUnitVector(this.leftFootRotation).y;
		if (offset < 0) {
			return 0;
		} else if (offset > dynamicDisplacementCutoff) {
			return dynamicDisplacementCutoff;
		}
		return offset;
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		if (offset < 0) {
			return 0;
		} else if (offset > dynamicDisplacementCutoff) {
			return dynamicDisplacementCutoff;
		}
		return offset;
	}

	// get the unit vector of the given rotation
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.getRotationColumn(2).normalize();
	}
}
