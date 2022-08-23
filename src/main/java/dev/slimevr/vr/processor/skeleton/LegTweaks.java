package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class LegTweaks {
	// state variables
	private float floorLevel;
	private float waistToFloorDist;
	private float currentDisengagementOffset = 0.0f;
	private boolean initialized = true;
	private boolean enabled = true; // master switch
	private boolean floorclipEnabled = false;
	private boolean skatingCorrectionEnabled = false;
	private boolean active = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;

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

	private Vector3f leftFootAcceleration = new Vector3f();
	private Vector3f rightFootAcceleration = new Vector3f();
	private Vector3f leftLowerLegAcceleration = new Vector3f();
	private Vector3f rightLowerLegAcceleration = new Vector3f();

	// knee placeholder
	private Vector3f leftKneePlaceholder = new Vector3f();
	private Vector3f rightKneePlaceholder = new Vector3f();
	private boolean kneesActive = false;

	/**
	 * here is a explination of each parameter that may need explaining
	 * STANDING_CUTOFF_VERTICAL is the percentage the waist has to be below its
	 * position at calibration to register as the user not standing
	 * MAX_DISENGAGMENT_OFFSET is how much the floor will be shifted to allow an
	 * offset to happen smoothly DYNAMIC_DISPLACEMENT_CUTOFF is the percent of
	 * downwards rotation that can contribute to dynamic displacment
	 * MAX_DYNAMIC_DISPLACMENT is the max amount the floor will be moved up to
	 * account for the foot rotating downward and needing to be put higher to
	 * avoid clipping in the gameworld MIN_ACCEPTABLE_ERROR and
	 * MAX_ACCEPTABLE_ERROR Defines the disitance where CORRECTION_WEIGHT_MIN
	 * and CORRECTION_WEIGHT_MAX are calculating a percent of velocity to
	 * correct rather than using the min or max FLOOR_CALIBRATION_OFFSET is the
	 * amount the floor plane is shifted up. This can help the feet from
	 * floating slightly above the ground
	 */

	// hyperparameters (clip correction)
	private static final float DYNAMIC_DISPLACEMENT_CUTOFF = 0.8f;
	private static final float MAX_DYNAMIC_DISPLACEMENT = 0.08f;
	private static final float FLOOR_CALIBRATION_OFFSET = 0.02f;

	// hyperparameters (skating correction)
	private static final float MIN_ACCEPTABLE_ERROR = 0.05f;
	private static final float MAX_ACCEPTABLE_ERROR = LegTweakBuffer.SKATING_DISTANCE_CUTOFF;
	private static final float CORRECTION_WEIGHT_MIN = 0.25f;
	private static final float CORRECTION_WEIGHT_MAX = 0.70f;

	// hyperparameters (floating feet correction)
	private static final float FOOT_Y_CORRECTION_WEIGHT = 0.45f;
	private static final float FOOT_Y_MAX_ACCELERATION = 0.40f;
	private static final float FOOT_Y_DIFF_CUTOFF = 0.15f;

	// hyperparameters (knee / waist correction)
	private static final float KNEE_CORRECTION_WEIGHT = 0.0f; // may be usful in future
	private static final float KNEE_LATERAL_WEIGHT = 0.45f;
	private static final float WAIST_PUSH_WEIGHT = 0.2f;

	// hyperparameters (misc)
	private static final float NEARLY_ZERO = 0.005f;
	private static final float STANDING_CUTOFF_VERTICAL = 0.65f;
	private static final float MAX_DISENGAGMENT_OFFSET = 0.30f;

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
		this.bufferInvalid = true;
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
		}

		// get the vector for acceleration of the feet and knees
		// (if feet are not available, fallback to 6 tracker mode)
		if (skeleton.leftFootTracker != null && skeleton.rightFootTracker != null) {
			skeleton.leftFootTracker.getAcceleration(leftFootAcceleration);
			skeleton.rightFootTracker.getAcceleration(rightFootAcceleration);
		} else {
			leftFootAcceleration.set(0, 0, 0);
			rightFootAcceleration.set(0, 0, 0);
		}

		if (skeleton.leftLowerLegTracker != null && skeleton.rightLowerLegTracker != null) {
			skeleton.leftLowerLegTracker.getAcceleration(leftLowerLegAcceleration);
			skeleton.rightLowerLegTracker.getAcceleration(rightLowerLegAcceleration);
		} else {
			leftLowerLegAcceleration.set(0, 0, 0);
			rightLowerLegAcceleration.set(0, 0, 0);
		}
	}

	// updates the object with the latest data from the skeleton
	private boolean preUpdate() {
		// populate the vectors with the latest data
		setVectors();

		// if not initialized, we need to calculate some values from this frame
		// to be used later (must happen immediately after reset)
		if (!initialized) {
			floorLevel = (leftFootPosition.y + rightFootPosition.y) / 2f + FLOOR_CALIBRATION_OFFSET;
			waistToFloorDist = waistPosition.y - floorLevel;

			// invalidate the buffer since the non initialized output may be
			// very wrong
			bufferInvalid = true;
			initialized = true;
		}

		// if not enabled do nothing and return false
		if (!enabled) {
			return false;
		}

		// if the user is standing start checking for a good time to enable leg
		// tweaks
		if (isStanding()) {
			active = true;
		} else {
			active = false;
		}

		// if the buffer is invalid set it up
		if (bufferInvalid) {
			bufferHead.setLeftFootPositionCorrected(leftFootPosition);
			bufferHead.setRightFootPositionCorrected(rightFootPosition);
			bufferHead.setLeftKneePositionCorrected(leftKneePosition);
			bufferHead.setRightKneePositionCorrected(rightKneePosition);
			bufferHead.setWaistPositionCorrected(waistPosition);
			bufferHead.setLeftFootPosition(leftFootPosition);
			bufferHead.setRightFootPosition(rightFootPosition);
			bufferHead.setLeftKneePosition(leftKneePosition);
			bufferHead.setRightKneePosition(rightKneePosition);
			bufferHead.setWaistPosition(waistPosition);
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
			skeleton.trackerWaistNode.localTransform.getTranslation(temp3);
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

		// put the acceleration vector that is applicable to the tracker
		// quantity in the the buffer
		// (if feet are not available, fallback to 6 tracker mode)
		if (skeleton.leftFootTracker != null && skeleton.rightFootTracker != null) {
			currentFrame.setLeftFootAcceleration(leftFootAcceleration);
			currentFrame.setRightFootAcceleration(rightFootAcceleration);
			currentFrame.setDetectionMode(LegTweakBuffer.FOOT_ACCEL);
		} else if (skeleton.leftLowerLegTracker != null && skeleton.rightLowerLegTracker != null) {
			currentFrame.setLeftFootAcceleration(leftLowerLegAcceleration);
			currentFrame.setRightFootAcceleration(rightLowerLegAcceleration);
			currentFrame.setDetectionMode(LegTweakBuffer.ANKLE_ACCEL);
		}

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

		// push the feet up if needed
		if (floorclipEnabled) {
			correctClipping();
		}

		// calculate acceleration and velocity of the feet using the buffer
		// (only needed if skating correction is enabled)
		if (skatingCorrectionEnabled) {
			correctSkating();
			correctFloat();
		}

		// determine if either leg is in a position to activate or deactivate
		// (use the buffer to get the positions before corrections)
		float leftFootDif = bufferHead
			.getLeftFootPosition(null)
			.subtract(leftFootPosition)
			.setX(0)
			.setZ(0)
			.length();

		float rightFootDif = bufferHead
			.getRightFootPosition(null)
			.subtract(rightFootPosition)
			.setX(0)
			.setZ(0)
			.length();

		if (!active && leftFootDif < NEARLY_ZERO) {
			leftLegActive = false;
		} else if (active && leftFootDif < NEARLY_ZERO) {
			leftLegActive = true;
		}

		if (!active && rightFootDif < NEARLY_ZERO) {
			rightLegActive = false;
		} else if (active && rightFootDif < NEARLY_ZERO) {
			rightLegActive = true;
		}

		// restore the y positions of inactive legs
		if (!leftLegActive) {
			leftFootPosition.y = bufferHead.getLeftFootPosition(null).y;
			leftKneePosition.y = bufferHead.getLeftKneePosition(null).y;
		}

		if (!rightLegActive) {
			rightFootPosition.y = bufferHead.getRightFootPosition(null).y;
			rightKneePosition.y = bufferHead.getRightKneePosition(null).y;
		}
		// calculate the correction for the knees
		if (kneesActive && initialized) {
			solveLowerBody();
		}

		// push the waist up the floor offset
		waistPosition.y += FLOOR_CALIBRATION_OFFSET;

		// populate the corrected data into the current frame
		this.bufferHead.setLeftFootPositionCorrected(leftFootPosition);
		this.bufferHead.setRightFootPositionCorrected(rightFootPosition);
		this.bufferHead.setLeftKneePositionCorrected(leftKneePosition);
		this.bufferHead.setRightKneePositionCorrected(rightKneePosition);
		this.bufferHead.setWaistPositionCorrected(waistPosition);
	}

	// returns true if the foot is clipped and false if it is not
	public boolean isClipped(float leftOffset, float rightOffset) {
		return (leftFootPosition.y < floorLevel + leftOffset
			|| rightFootPosition.y < floorLevel + rightOffset);
	}

	// corrects the foot position to be above the floor level that is calculated
	// on calibration
	private void correctClipping() {
		// calculate how angled down the feet are as a scalar value between 0
		// and 1 (0 = flat, 1 = max angle)
		float leftOffset = getLeftFootOffset();
		float rightOffset = getRightFootOffset();
		float avgOffset = 0;

		// if there is no clipping, or clipping is not enabled, return false
		if (!isClipped(leftOffset, rightOffset) || !enabled) {
			return;
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
			avgOffset += displacement;
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
			avgOffset += displacement;

		}

		waistPosition.y += (avgOffset / 2) * WAIST_PUSH_WEIGHT;
	}

	// based on the data from the last frame compute a new position that reduces
	// ice skating
	private void correctSkating() {
		// for either foot that is locked get its position (x and z only we let
		// y move freely) and set it to be there
		if (bufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			leftFootPosition.x = bufferHead
				.getParent()
				.getLeftFootPositionCorrected(null).x;

			leftFootPosition.z = bufferHead
				.getParent()
				.getLeftFootPositionCorrected(null).z;
		}

		if (bufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			rightFootPosition.x = bufferHead
				.getParent()
				.getRightFootPositionCorrected(null).x;

			rightFootPosition.z = bufferHead
				.getParent()
				.getRightFootPositionCorrected(null).z;
		}

		// for either foot that is unlocked get its last position and calculate
		// its position for this frame. the amount of displacement is based on
		// the distance between the last position, the current position, and
		// the hyperparameters
		correctUnlockedLeftFootTracker();
		correctUnlockedRightFootTracker();
	}

	private void correctUnlockedLeftFootTracker() {
		if (bufferHead.getLeftLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f leftFootDif = leftFootPosition
				.subtract(bufferHead.getParent().getLeftFootPositionCorrected(null))
				.setY(0);

			if (leftFootDif.length() > NEARLY_ZERO) {
				float leftY = leftFootPosition.y;

				Vector3f temp = bufferHead.getParent().getLeftFootPositionCorrected(null);
				Vector3f velocity = bufferHead.getLeftFootVelocity(null);

				// first add the difference from the last frame to this frame
				temp = temp
					.subtract(
						bufferHead
							.getParent()
							.getLeftFootPosition(null)
							.subtract(bufferHead.getLeftFootPosition(null))
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
					bufferHead.getParent().getLeftFootPositionCorrected(null)
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
						this.bufferHead.getLeftFootPosition(null).x,
						this.bufferHead.getParent().getLeftFootPositionCorrected(null).x,
						leftFootPosition.x
					)
				) {
					leftFootPosition.x = bufferHead.getLeftFootPosition(null).x;
				}

				if (
					checkOverShoot(
						this.bufferHead.getLeftFootPosition(null).z,
						this.bufferHead.getParent().getLeftFootPositionCorrected(null).z,
						leftFootPosition.z
					)
				) {
					leftFootPosition.z = bufferHead.getLeftFootPosition(null).z;
				}
			}
		}
	}

	private void correctUnlockedRightFootTracker() {
		if (bufferHead.getRightLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3f rightFootDif = rightFootPosition
				.subtract(bufferHead.getParent().getRightFootPositionCorrected(null))
				.setY(0);

			if (rightFootDif.length() > NEARLY_ZERO) {
				float rightY = rightFootPosition.y;
				Vector3f temp = bufferHead
					.getParent()
					.getRightFootPositionCorrected(null);
				Vector3f velocity = bufferHead.getRightFootVelocity(null);

				// first add the difference from the last frame to this frame
				temp = temp
					.subtract(
						bufferHead
							.getParent()
							.getRightFootPosition(null)
							.subtract(bufferHead.getRightFootPosition(null))
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
					bufferHead.getParent().getRightFootPositionCorrected(null)
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
						this.bufferHead.getRightFootPosition(null).x,
						this.bufferHead.getParent().getRightFootPositionCorrected(null).x,
						rightFootPosition.x
					)
				) {
					rightFootPosition.x = bufferHead.getRightFootPosition(null).x;
				}


				if (
					checkOverShoot(
						this.bufferHead.getRightFootPosition(null).z,
						this.bufferHead.getParent().getRightFootPositionCorrected(null).z,
						rightFootPosition.z
					)
				) {
					rightFootPosition.z = bufferHead.getRightFootPosition(null).z;
				}
			}
		}
	}

	// check if the foot is begining to float over the floor when it should not
	// be if it is move it back down to the floor
	// note: very small corrections are all that is allowed to prevent momemnt
	// that looks unrealistic
	private void correctFloat() {
		boolean correctLeft = true;
		boolean correctRight = true;


		if (bufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			float lastPositionY = bufferHead
				.getParent()
				.getLeftFootPositionCorrected(null).y;
			if (
				Math.abs(leftFootPosition.y - lastPositionY) < FOOT_Y_DIFF_CUTOFF
					&& leftFootPosition.y > lastPositionY
					&& bufferHead.getLeftFootAccelerationY() < FOOT_Y_MAX_ACCELERATION
			) {
				leftFootPosition.y = lastPositionY;
				correctLeft = false;
			}
		}


		if (bufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			float lastPositionY = bufferHead
				.getParent()
				.getRightFootPositionCorrected(null).y;


			if (
				Math.abs(rightFootPosition.y - lastPositionY) < FOOT_Y_DIFF_CUTOFF
					&& rightFootPosition.y > lastPositionY
					&& bufferHead.getRightFootAccelerationY() < FOOT_Y_MAX_ACCELERATION
			) {
				rightFootPosition.y = lastPositionY;
				correctRight = false;
			}
		}

		// using the velocity correct the position
		if (correctLeft) {
			correctLeftFootTrackerY();
		}

		if (correctRight) {
			correctRightFootTrackerY();
		}
	}

	private void correctLeftFootTrackerY() {
		Vector3f temp;
		Vector3f leftFootDif = leftFootPosition
			.subtract(bufferHead.getParent().getLeftFootPositionCorrected(null));
		if (Math.abs(leftFootDif.y) > NEARLY_ZERO) {
			temp = bufferHead.getParent().getLeftFootPositionCorrected(null)
				.subtract(
					bufferHead
						.getParent()
						.getLeftFootPosition(null)
						.subtract(bufferHead.getLeftFootPosition(null))
				);


			float leftFloor = (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getLeftFootOffset()))
				- currentDisengagementOffset;


			if (
				temp.y
					< leftFloor
			) {
				leftFootPosition.y = leftFloor;
			} else {
				leftFootPosition.y = temp.y;
			}

			Vector3f velocity = bufferHead.getLeftFootVelocity(null);



			if (velocity.y * leftFootDif.y > 0) {
				leftFootPosition.y += velocity.y * FOOT_Y_CORRECTION_WEIGHT;
			} else {
				leftFootPosition.y -= velocity.y * FOOT_Y_CORRECTION_WEIGHT;
			}



			// check for overshoot and correct if necessary
			if (
				checkOverShoot(
					this.bufferHead.getLeftFootPosition(null).y,
					this.bufferHead.getParent().getLeftFootPositionCorrected(null).y,
					leftFootPosition.y
				)
			) {
				leftFootPosition.y = bufferHead.getLeftFootPosition(null).y;
			}
		}
	}

	private void correctRightFootTrackerY() {
		Vector3f temp;
		Vector3f rightFootDif = rightFootPosition
			.subtract(bufferHead.getParent().getRightFootPositionCorrected(null));



		if (Math.abs(rightFootDif.y) > NEARLY_ZERO) {
			temp = bufferHead.getParent().getRightFootPositionCorrected(null)
				.subtract(
					bufferHead
						.getParent()
						.getRightFootPosition(null)
						.subtract(bufferHead.getRightFootPosition(null))
				);



			float rightFloor = (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getRightFootOffset()))
				- currentDisengagementOffset;



			if (
				temp.y
					< rightFloor
			) {
				rightFootPosition.y = rightFloor;
			} else {
				rightFootPosition.y = temp.y;
			}



			Vector3f velocity = bufferHead.getRightFootVelocity(null);



			if (velocity.y * rightFootDif.y > 0) {
				rightFootPosition.y += velocity.y * FOOT_Y_CORRECTION_WEIGHT;
			} else {
				rightFootPosition.y -= velocity.y * FOOT_Y_CORRECTION_WEIGHT;
			}



			// check for overshoot and correct if necessary
			if (
				checkOverShoot(
					this.bufferHead.getRightFootPosition(null).y,
					this.bufferHead.getParent().getRightFootPositionCorrected(null).y,
					rightFootPosition.y
				)
			) {
				rightFootPosition.y = bufferHead.getRightFootPosition(null).y;
			}
		}
	}

	// returns true if it is likely the user is standing
	public boolean isStanding() {
		// if the waist is below the vertical cutoff, user is not standing
		float cutoff = floorLevel
			+ waistToFloorDist
			- (waistToFloorDist * STANDING_CUTOFF_VERTICAL);



		if (waistPosition.y < cutoff) {
			currentDisengagementOffset = (1 - waistPosition.y / cutoff)
				* MAX_DISENGAGMENT_OFFSET;
			return false;
		}



		currentDisengagementOffset = 0f;



		return true;
	}

	// move the knees in to a position that is closer to the truth
	private void solveLowerBody() {
		// calculate the left and right waist nodes in standing space
		Vector3f leftWaist = waistPosition;// .add(leftWaistUpperLegOffset);
		Vector3f rightWaist = waistPosition;// .add(rightWaistUpperLegOffset);

		Vector3f tempLeft;
		Vector3f tempRight;

		// before moveing the knees back closer to the waist nodes offset them
		// the same amount the foot trackers where offset
		float leftXDif = leftFootPosition.x - bufferHead.getLeftFootPosition(null).x;
		float rightXDif = rightFootPosition.x - bufferHead.getRightFootPosition(null).x;
		float leftYDif = leftFootPosition.y - bufferHead.getLeftFootPosition(null).y;
		float rightYDif = rightFootPosition.y - bufferHead.getRightFootPosition(null).y;
		leftKneePosition.x += leftXDif * KNEE_LATERAL_WEIGHT;
		leftKneePosition.y += leftYDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.x += rightXDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.y += rightYDif * KNEE_LATERAL_WEIGHT;


		// calculate the bone distances
		float leftKneeWaist = bufferHead.getLeftKneePosition(null).distance(leftWaist);
		float rightKneeWaist = bufferHead.getRightKneePosition(null).distance(rightWaist);

		float leftKneeWaistNew = leftKneePosition.distance(leftWaist);
		float rightKneeWaistNew = rightKneePosition.distance(rightWaist);
		float leftKneeOffset = leftKneeWaistNew - leftKneeWaist;
		float rightKneeOffset = rightKneeWaistNew - rightKneeWaist;

		// get the vector from the waist to the knee
		Vector3f leftKneeVector = leftKneePosition
			.subtract(leftWaist)
			.normalize()
			.mult(leftKneeOffset * KNEE_CORRECTION_WEIGHT);



		Vector3f rightKneeVector = rightKneePosition
			.subtract(rightWaist)
			.normalize()
			.mult(rightKneeOffset * KNEE_CORRECTION_WEIGHT);

		// correct the knees
		tempLeft = leftKneePosition.subtract(leftKneeVector);
		tempRight = rightKneePosition.subtract(rightKneeVector);
		leftKneePosition.set(tempLeft);
		rightKneePosition.set(tempRight);
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
		return (trueVal - valBefore) * (trueVal - valAfter) < 0;
	}

	// get the unit vector of the given rotation
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.getRotationColumn(2).normalize();
	}
}
