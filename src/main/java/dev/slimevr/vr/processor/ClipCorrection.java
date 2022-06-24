package dev.slimevr.vr.processor;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.processor.skeleton.LegTweakBuffer;
import com.jme3.math.FastMath;


public class ClipCorrection {
	// class vars
	private float floorLevel;
	private float waistToFloorDist;
	private float currentDisengagementOffset = 0.0f;
	// state variables
	private boolean initialized = true;
	private boolean enabled = true;
	private boolean active = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;
	static final Quaternion FORWARD_QUATERNION = new Quaternion()
		.fromAngles(FastMath.HALF_PI, 0, 0);

	// leg data
	private Vector3f leftFootPosition = new Vector3f();
	private Vector3f rightFootPosition = new Vector3f();
	private Vector3f leftKneePosition = new Vector3f();
	private Vector3f rightKneePosition = new Vector3f();
	private Vector3f waistPosition = new Vector3f();
	private Quaternion leftFootRotation = new Quaternion();
	private Quaternion rightFootRotation = new Quaternion();
	private Vector3f leftWaistUpperLegOffset = new Vector3f();
	private Vector3f rightWaistUpperLegOffset = new Vector3f();

	// hyperparameters (clip correction)
	private static final float STANDING_CUTOFF_HORIZONTAL = 0.5f;
	private static final float STANDING_CUTOFF_VERTICAL = 0.35f;
	private static final float MAX_DYNAMIC_DISPLACEMENT = 0.04f;
	private static final float MAX_DISENGAGMENT_OFFSET = 0.25f;
	private static final float DYNAMIC_DISPLACEMENT_CUTOFF = 0.7f;


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

	public boolean getActive() {
		return this.active;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return this.enabled;
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
			active = false;

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
			active = true;
		}

		// first update the buffer
		LegTweakBuffer currentFrame = new LegTweakBuffer();
		currentFrame.setLeftFootPosition(leftFootPosition);
		currentFrame.setLeftFootRotation(leftFootRotation);
		currentFrame.setLeftKneePosition(leftKneePosition);
		currentFrame.setRightFootPosition(rightFootPosition);
		currentFrame.setRightFootRotation(rightFootRotation);
		currentFrame.setRightKneePosition(rightKneePosition);
		currentFrame.setWaistPosition(waistPosition);

		// calculate acceleration and velocity of the feet using the buffer
		currentFrame.setParent(legBufferHead);
		this.legBufferHead = currentFrame;
		currentFrame.calculateFootAttributes(active);


		// now correct the position of the legs from the last frames data
		boolean corrected2 = correctSkating();

		// once done run the clip correction (also corrects the position of the
		// knees)
		boolean corrected1 = correctClipping();

		// determine if either leg is in a position to activate or deactivate
		// (use the buffer to get the positions before corrections)

		float leftFootDif = legBufferHead.getLeftFootPosition().subtract(leftFootPosition).length();
		float rightFootDif = legBufferHead
			.getRightFootPosition()
			.subtract(rightFootPosition)
			.length();
		if (!active && leftFootDif < 0.005f) {
			leftLegActive = false;
		} else if (active && leftFootDif < 0.005f) {
			leftLegActive = true;
		}
		if (!active && rightFootDif < 0.005f) {
			rightLegActive = false;
		} else if (active && rightFootDif < 0.005f) {
			rightLegActive = true;
		}

		// restore the positions of inactive legs
		if (!leftLegActive) {
			leftFootPosition = legBufferHead.getLeftFootPosition();
			leftKneePosition = legBufferHead.getLeftKneePosition();
		}
		if (!rightLegActive) {
			rightFootPosition = legBufferHead.getRightFootPosition();
			rightKneePosition = legBufferHead.getRightKneePosition();
		}

		// populate the corrected data into the current frame
		this.legBufferHead.setLeftFootPositionCorrected(leftFootPosition);
		this.legBufferHead.setRightFootPositionCorrected(rightFootPosition);
		this.legBufferHead.setLeftKneePositionCorrected(leftKneePosition);
		this.legBufferHead.setRightKneePositionCorrected(rightKneePosition);
		this.legBufferHead.setWaistPositionCorrected(waistPosition);
		this.legBufferHead
			.setLeftFloorLevel(
				(floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getLeftFootOffset()))
					- currentDisengagementOffset
			);
		this.legBufferHead
			.setRightFloorLevel(
				(floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getRightFootOffset()))
					- currentDisengagementOffset
			);

		return corrected1 || corrected2;
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
		if (
			leftFootPosition.y
				< (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * leftOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (MAX_DYNAMIC_DISPLACEMENT * leftOffset)
						- leftFootPosition.y
						- currentDisengagementOffset
				);
			leftFootPosition.y += displacement;
			leftKneePosition.y += displacement;
		}
		if (
			rightFootPosition.y
				< (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * rightOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (MAX_DYNAMIC_DISPLACEMENT * rightOffset)
						- rightFootPosition.y
						- currentDisengagementOffset
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

	// based on the data from the last frame compute a new position that reduces
	// ice skating
	public boolean correctSkating() {
		// for either foot that is locked get its position (x and z only we let
		// y move freely) and set it to be there
		if (legBufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			leftFootPosition.x = legBufferHead.getParent().getLeftFootPositionCorrected().x;
			leftFootPosition.z = legBufferHead.getParent().getLeftFootPositionCorrected().z;
		}
		if (legBufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			rightFootPosition.x = legBufferHead.getParent().getRightFootPositionCorrected().x;
			rightFootPosition.z = legBufferHead.getParent().getRightFootPositionCorrected().z;
		}

		// for either foot that is unlocked get its last position and calculate
		// its position for this frame. The approch for now is to allow for a
		// 10% change to the velocity to gradually correct without snapping in
		// to place
		if (legBufferHead.getLeftLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f leftFootDif = leftFootPosition
				.subtract(legBufferHead.getParent().getLeftFootPositionCorrected())
				.setY(0);
			if (leftFootDif.length() > 0.01f) {
				Vector3f velocity = legBufferHead.getLeftFootVelocity();
				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				if (velocity.x * leftFootDif.x > 0) {
					leftFootDif.x += velocity.x * 0.1f;
				} else {
					leftFootDif.x -= velocity.x * 0.1f;
				}
				if (velocity.z * leftFootDif.z > 0) {
					leftFootDif.z += velocity.z * 0.1f;
				} else {
					leftFootDif.z -= velocity.z * 0.1f;
				}
			}
		}
		if (legBufferHead.getRightLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f rightFootDif = rightFootPosition
				.subtract(legBufferHead.getParent().getRightFootPositionCorrected())
				.setY(0);
			if (rightFootDif.length() > 0.01f) {
				Vector3f velocity = legBufferHead.getRightFootVelocity();
				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				if (velocity.x * rightFootDif.x > 0) {
					rightFootDif.x += velocity.x * 0.1f;
				} else {
					rightFootDif.x -= velocity.x * 0.1f;
				}
				if (velocity.z * rightFootDif.z > 0) {
					rightFootDif.z += velocity.z * 0.1f;
				} else {
					rightFootDif.z -= velocity.z * 0.1f;
				}
			}
		}
		return true;

	}

	// returns true if it is likly the user is standing
	public boolean isStanding() {
		// if the waist is below the verticalcutoff, we are not standing
		float cutoff = floorLevel
			+ waistToFloorDist
			- (waistToFloorDist * STANDING_CUTOFF_VERTICAL);
		if (waistPosition.y < cutoff) {
			currentDisengagementOffset = (1 - waistPosition.y / cutoff)
				* MAX_DISENGAGMENT_OFFSET;
			return false;
		}
		currentDisengagementOffset = 0f;
		// if the waist is above the verticalcutoff, we are standing as long as
		// the horizontal cutoff is not exceeded on both feet
		Vector3f left = leftFootPosition.clone();
		Vector3f right = leftFootPosition.clone();
		Vector3f waist = waistPosition.clone();
		left.y = 0;
		right.y = 0;
		waist.y = 0;
		return !(waist.distance(left) > STANDING_CUTOFF_HORIZONTAL
			&& waist.distance(right) > STANDING_CUTOFF_HORIZONTAL);
	}

	private float getLeftFootOffset() {
		float offset = computeUnitVector(this.leftFootRotation).y;
		if (offset < 0) {
			return 0;
		} else if (offset > DYNAMIC_DISPLACEMENT_CUTOFF) {
			return DYNAMIC_DISPLACEMENT_CUTOFF;
		}
		return offset;
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		if (offset < 0) {
			return 0;
		} else if (offset > DYNAMIC_DISPLACEMENT_CUTOFF) {
			return DYNAMIC_DISPLACEMENT_CUTOFF;
		}
		return offset;
	}

	// get the unit vector of the given rotation
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.getRotationColumn(2).normalize();
	}
}
