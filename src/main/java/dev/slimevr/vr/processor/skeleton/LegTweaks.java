package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class LegTweaks {
	// class vars
	private float floorLevel;
	private float waistToFloorDist;
	private float currentDisengagementOffset = 0.0f;
	// state variables
	private boolean initialized = true;
	private boolean enabled = true; // master switch
	private boolean floorclipEnabled = false;
	private boolean skatingCorrectionEnabled = false;
	private boolean active = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;
	private boolean positionsPopulated = false;

	// skeleton
	private HumanSkeleton skeleton;

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

	// knee placeholder
	private Vector3f leftKneePlaceholder = new Vector3f();
	private Vector3f rightKneePlaceholder = new Vector3f();
	private boolean kneesActive = false;

	// hyperparameters (clip correction)
	private static final float STANDING_CUTOFF_HORIZONTAL = 0.5f;
	private static final float STANDING_CUTOFF_VERTICAL = 0.35f;
	private static final float MAX_DYNAMIC_DISPLACEMENT = 0.08f;
	private static final float MAX_DISENGAGMENT_OFFSET = 0.25f;
	private static final float DYNAMIC_DISPLACEMENT_CUTOFF = 0.8f;

	// hyperparameters (skating correction)
	private static final float MIN_ACCEPTABLE_ERROR = 0.075f;
	private static final float MAX_ACCEPTABLE_ERROR = LegTweakBuffer.SKATING_CUTOFF;
	private static final float CORRECTION_WEIGHT_MIN = 0.15f;
	private static final float CORRECTION_WEIGHT_MAX = 0.7f;


	// buffer for holding previus frames of data
	private LegTweakBuffer bufferHead = new LegTweakBuffer();
	private boolean bufferInvalid = true;

	public LegTweaks(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	// update the offsets for the waist and upper leg
	// this is used for correcting the knee tracker position
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

	public void setFloorclipEnabled(boolean floorclipEnabled) {
		this.floorclipEnabled = floorclipEnabled;
		// reset the buffer
		this.bufferHead = new LegTweakBuffer();
	}

	public void setSkatingReductionEnabled(boolean skatingCorrectionEnabled) {
		this.skatingCorrectionEnabled = skatingCorrectionEnabled;
		// reset the buffer
		this.bufferHead = new LegTweakBuffer();
		this.bufferInvalid = true;
	}

	public boolean getEnabled() {
		return this.enabled;
	}

	public boolean getFloorclipEnabled() {
		return this.floorclipEnabled;
	}

	public boolean getSkatingReductionEnabled() {
		return this.skatingCorrectionEnabled;
	}

	public void resetBuffer() {
		bufferInvalid = true;
	}

	// set the vectors in this object to the vectors in the skeleton
	private void setVectors() {
		// set the positions of the feet and knees to the skeletons current
		// positions
		if (skeleton.computedLeftKneeTracker != null || skeleton.computedRightKneeTracker != null) {
			kneesActive = true;
			leftKneePosition = skeleton.computedLeftKneeTracker.position;
			rightKneePosition = skeleton.computedRightKneeTracker.position;
		} else {
			kneesActive = false;
			leftKneePosition = leftKneePlaceholder;
			rightKneePosition = rightKneePlaceholder;
		}
		if (
			skeleton.computedLeftFootTracker != null
				&& skeleton.computedRightFootTracker != null
				&& skeleton.computedWaistTracker != null
		) {
			leftFootPosition = skeleton.computedLeftFootTracker.position;
			rightFootPosition = skeleton.computedRightFootTracker.position;
			waistPosition = skeleton.computedWaistTracker.position;
			leftFootRotation = skeleton.computedLeftFootTracker.rotation;
			rightFootRotation = skeleton.computedRightFootTracker.rotation;
			positionsPopulated = true;
		}
	}

	// updates the object with the latest data from the skeleton
	private boolean preUpdate() {
		// populate the vectors with the latest data
		setVectors();

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
		}
		// if the user has the majority of their weight on their feet
		// start checking for a good time to enable the floor clip without
		// causing skipping
		else {
			active = true;
		}
		// if the buffer is invalid set it up
		if (bufferInvalid) {
			bufferHead.setLeftFootPositionCorrected(leftFootPosition);
			bufferHead.setRightFootPositionCorrected(rightFootPosition);
			bufferHead.setLeftLegState(LegTweakBuffer.UNLOCKED);
			bufferHead.setRightLegState(LegTweakBuffer.UNLOCKED);
			bufferInvalid = false;
		}

		// update offsets for knee correction if the knees are not null
		if (kneesActive) {
			Vector3f temp1 = new Vector3f();
			Vector3f temp2 = new Vector3f();
			Vector3f temp3 = new Vector3f();

			// get offsets from the waist to the upper legs
			skeleton.leftHipNode.localTransform.getTranslation(temp1);
			skeleton.rightHipNode.localTransform.getTranslation(temp2);
			skeleton.waistNode.localTransform.getTranslation(temp3);
			updateOffsets(temp1, temp2, temp3);
		}
		// update the buffer
		LegTweakBuffer currentFrame = new LegTweakBuffer();
		currentFrame.setLeftFootPosition(leftFootPosition);
		currentFrame.setLeftFootRotation(leftFootRotation);
		currentFrame.setLeftKneePosition(leftKneePosition);
		currentFrame.setRightFootPosition(rightFootPosition);
		currentFrame.setRightFootRotation(rightFootRotation);
		currentFrame.setRightKneePosition(rightKneePosition);
		currentFrame.setWaistPosition(waistPosition);

		currentFrame.setParent(bufferHead);
		this.bufferHead = currentFrame;
		this.bufferHead.calculateFootAttributes(active);

		return true;
	}

	// tweak the position of the legs based on data from the last frames
	public void tweakLegs() {
		// update the class with the latest data from the skeleton
		// if false is returned something indicated that the legs should not be
		// tweaked
		if (!preUpdate()) {
			return;
		}

		boolean corrected1 = false;
		boolean corrected2 = false;
		// push the feet up if needed
		if (floorclipEnabled) {
			corrected1 = correctClipping();
		}

		// calculate acceleration and velocity of the feet using the buffer
		// (only needed if skating correction is enabled)
		if (skatingCorrectionEnabled) {
			corrected2 = correctSkating();
		}

		// determine if either leg is in a position to activate or deactivate
		// (use the buffer to get the positions before corrections)
		float leftFootDif = bufferHead
			.getLeftFootPosition()
			.subtract(leftFootPosition)
			.setX(0)
			.setZ(0)
			.length();
		float rightFootDif = bufferHead
			.getRightFootPosition()
			.subtract(rightFootPosition)
			.setX(0)
			.setZ(0)
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

		// restore the y positions of inactive legs
		if (!leftLegActive) {
			leftFootPosition.y = bufferHead.getLeftFootPosition().y;
			leftKneePosition.y = bufferHead.getLeftKneePosition().y;
		}
		if (!rightLegActive) {
			rightFootPosition.y = bufferHead.getRightFootPosition().y;
			rightKneePosition.y = bufferHead.getRightKneePosition().y;
		}

		// calculate the correction for the knees
		if (kneesActive) {
			correctKnees();
		}

		// populate the corrected data into the current frame
		this.bufferHead.setLeftFootPositionCorrected(leftFootPosition);
		this.bufferHead.setRightFootPositionCorrected(rightFootPosition);
		this.bufferHead.setLeftKneePositionCorrected(leftKneePosition);
		this.bufferHead.setRightKneePositionCorrected(rightKneePosition);
		this.bufferHead.setWaistPositionCorrected(waistPosition);
		this.bufferHead
			.setLeftFloorLevel(
				(floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getLeftFootOffset()))
					- currentDisengagementOffset
			);
		this.bufferHead
			.setRightFloorLevel(
				(floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getRightFootOffset()))
					- currentDisengagementOffset
			);
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

		// move the feet to their new positions
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

		return true;
	}

	// based on the data from the last frame compute a new position that reduces
	// ice skating
	public boolean correctSkating() {
		// for either foot that is locked get its position (x and z only we let
		// y move freely) and set it to be there
		if (bufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			leftFootPosition.x = bufferHead.getParent().getLeftFootPositionCorrected().x;
			leftFootPosition.z = bufferHead.getParent().getLeftFootPositionCorrected().z;
		}
		if (bufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			rightFootPosition.x = bufferHead.getParent().getRightFootPositionCorrected().x;
			rightFootPosition.z = bufferHead.getParent().getRightFootPositionCorrected().z;
		}

		// for either foot that is unlocked get its last position and calculate
		// its position for this frame. the amount of displacement is based on
		// the distance between the last position and the current position and
		// the hyperparameters
		if (bufferHead.getLeftLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f leftFootDif = leftFootPosition
				.subtract(bufferHead.getParent().getLeftFootPositionCorrected())
				.setY(0);
			if (leftFootDif.length() > 0.005f) {
				float leftY = leftFootPosition.y;

				Vector3f temp = bufferHead.getParent().getLeftFootPositionCorrected();
				Vector3f velocity = bufferHead.getLeftFootVelocity();
				// first add the difference from the last frame to this frame
				temp = temp
					.subtract(
						bufferHead
							.getParent()
							.getLeftFootPosition()
							.subtract(bufferHead.getLeftFootPosition())
					);
				leftFootPosition.y = leftY;
				leftFootPosition.x = temp.x;
				leftFootPosition.z = temp.z;

				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				// calculate the correction weight
				float weight = calculateCorrectionWeight(
					leftFootPosition,
					bufferHead.getParent().getLeftFootPositionCorrected()
				);
				if (velocity.x * leftFootDif.x > 0) {
					leftFootPosition.x += velocity.x * weight;
				} else {
					leftFootPosition.x -= velocity.x * weight;
				}
				if (velocity.z * leftFootDif.z > 0) {
					leftFootPosition.z += velocity.z * weight;
				} else {
					leftFootPosition.z -= velocity.z * weight;
				}
				// if the foot overshot the target, move it back to the target
				if (
					checkOverShoot(
						this.bufferHead.getLeftFootPosition().x,
						this.bufferHead.getParent().getLeftFootPositionCorrected().x,
						leftFootPosition.x
					)
				) {
					leftFootPosition.x = bufferHead.getLeftFootPosition().x;
				}
				if (
					checkOverShoot(
						this.bufferHead.getLeftFootPosition().z,
						this.bufferHead.getParent().getLeftFootPositionCorrected().z,
						leftFootPosition.z
					)
				) {
					leftFootPosition.z = bufferHead.getLeftFootPosition().z;
				}


			}
		}
		if (bufferHead.getRightLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f rightFootDif = rightFootPosition
				.subtract(bufferHead.getParent().getRightFootPositionCorrected())
				.setY(0);
			if (rightFootDif.length() > 0.005f) {
				float rightY = rightFootPosition.y;
				Vector3f temp = bufferHead.getParent().getRightFootPositionCorrected();
				Vector3f velocity = bufferHead.getRightFootVelocity();
				// first add the difference from the last frame to this frame
				temp = temp
					.subtract(
						bufferHead
							.getParent()
							.getRightFootPosition()
							.subtract(bufferHead.getRightFootPosition())
					);
				rightFootPosition.y = rightY;
				rightFootPosition.x = temp.x;
				rightFootPosition.z = temp.z;

				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				// calculate the correction weight
				float weight = calculateCorrectionWeight(
					rightFootPosition,
					bufferHead.getParent().getRightFootPositionCorrected()
				);

				if (velocity.x * rightFootDif.x > 0) {
					rightFootPosition.x += velocity.x * weight;
				} else {
					rightFootPosition.x -= velocity.x * weight;
				}
				if (velocity.z * rightFootDif.z > 0) {
					rightFootPosition.z += velocity.z * weight;
				} else {
					rightFootPosition.z -= velocity.z * weight;
				}
				// if the foot overshot the target, move it back to the target
				if (
					checkOverShoot(
						this.bufferHead.getRightFootPosition().x,
						this.bufferHead.getParent().getRightFootPositionCorrected().x,
						rightFootPosition.x
					)
				) {
					rightFootPosition.x = bufferHead.getRightFootPosition().x;
				}
				if (
					checkOverShoot(
						this.bufferHead.getRightFootPosition().z,
						this.bufferHead.getParent().getRightFootPositionCorrected().z,
						rightFootPosition.z
					)
				) {
					rightFootPosition.z = bufferHead.getRightFootPosition().z;
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

	// move the knees in to a position that satisfys the distance between nodes
	private void correctKnees() {
		Vector3f leftWaist = waistPosition.add(leftWaistUpperLegOffset);
		Vector3f rightWaist = waistPosition.add(rightWaistUpperLegOffset);

		float leftKneeWaist = leftKneePosition.distance(leftWaist);
		float rightKneeWaist = rightKneePosition.distance(rightWaist);

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

		// correct the knees (not perfect by any means but very close and
		// computationally cheap)
		leftKneePosition = leftKneePosition.subtract(leftKneeVector);
		rightKneePosition = rightKneePosition.subtract(rightKneeVector);

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

	// calculate the weight of foot correction
	private float calculateCorrectionWeight(
		Vector3f foot,
		Vector3f footCorrected
	) {
		Vector3f footDif = foot.subtract(footCorrected).setY(0);
		if (footDif.length() < MIN_ACCEPTABLE_ERROR) {
			return CORRECTION_WEIGHT_MIN;
		} else if (footDif.length() > MAX_ACCEPTABLE_ERROR) {
			return CORRECTION_WEIGHT_MAX;
		}
		return CORRECTION_WEIGHT_MIN
			+ (footDif.length() - MIN_ACCEPTABLE_ERROR)
				/ (MAX_ACCEPTABLE_ERROR - MIN_ACCEPTABLE_ERROR)
				* (CORRECTION_WEIGHT_MAX - CORRECTION_WEIGHT_MIN);
	}

	// check if the difference between two floats flipped after correction
	private boolean checkOverShoot(float trueVal, float valBefore, float valAfter) {
		if ((valAfter - trueVal) * (valBefore - trueVal) < 0) {
			return true;
		}
		return false;
	}

	// get the unit vector of the given rotation
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.getRotationColumn(2).normalize();
	}
}
