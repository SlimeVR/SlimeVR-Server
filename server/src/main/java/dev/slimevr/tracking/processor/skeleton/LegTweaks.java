package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import dev.slimevr.config.LegTweaksConfig;
import dev.slimevr.tracking.processor.TransformNode;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;


public class LegTweaks {
	// state variables
	private float floorLevel;
	private float waistToFloorDist;
	private float currentDisengagementOffset = 0.0f;
	private static float currentCorrectionStrength = 0.3f; // default value
	private boolean initialized = true;
	private boolean enabled = true; // master switch
	private boolean floorclipEnabled = false;
	private boolean skatingCorrectionEnabled = false;
	private boolean active = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;

	// skeleton and config
	private HumanSkeleton skeleton;
	private LegTweaksConfig config;

	// leg data
	private Vector3 leftFootPosition = Vector3.Companion.getNULL();
	private Vector3 rightFootPosition = Vector3.Companion.getNULL();
	private Vector3 leftKneePosition = Vector3.Companion.getNULL();
	private Vector3 rightKneePosition = Vector3.Companion.getNULL();
	private Vector3 waistPosition = Vector3.Companion.getNULL();
	private Quaternion leftFootRotation = Quaternion.Companion.getIDENTITY();
	private Quaternion rightFootRotation = Quaternion.Companion.getIDENTITY();

	private Vector3 leftFootAcceleration = Vector3.Companion.getNULL();
	private Vector3 rightFootAcceleration = Vector3.Companion.getNULL();
	private Vector3 leftLowerLegAcceleration = Vector3.Companion.getNULL();
	private Vector3 rightLowerLegAcceleration = Vector3.Companion.getNULL();

	// knee placeholder
	private Vector3 leftKneePlaceholder = Vector3.Companion.getNULL();
	private Vector3 rightKneePlaceholder = Vector3.Companion.getNULL();

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
	static float DYNAMIC_DISPLACEMENT_CUTOFF = 1.0f;
	static float MAX_DYNAMIC_DISPLACEMENT = 0.06f;
	private static final float FLOOR_CALIBRATION_OFFSET = 0.015f;

	// hyperparameters (skating correction)
	private static final float MIN_ACCEPTABLE_ERROR = 0.01f;
	private static final float MAX_ACCEPTABLE_ERROR = 0.225f;
	private static final float CORRECTION_WEIGHT_MIN = 0.40f;
	private static final float CORRECTION_WEIGHT_MAX = 0.70f;
	private static final float CONTINUOUS_CORRECTION_DIST = 0.5f;
	private static final int CONTINUOUS_CORRECTION_WARMUP = 175;

	// hyperparameters (knee / waist correction)
	private static final float KNEE_CORRECTION_WEIGHT = 0.00f;
	private static final float KNEE_LATERAL_WEIGHT = 0.8f;
	private static final float WAIST_PUSH_WEIGHT = 0.2f;

	// hyperparameters (COM calculation)
	// mass percentages of the body
	private static final float HEAD_MASS = 0.082f;
	private static final float CHEST_MASS = 0.25f;
	private static final float WAIST_MASS = 0.209f;
	private static final float THIGH_MASS = 0.128f;
	private static final float CALF_MASS = 0.0535f;
	private static final float UPPER_ARM_MASS = 0.031f;
	private static final float FOREARM_MASS = 0.017f;

	// hyperparameters (misc)
	static final float NEARLY_ZERO = 0.001f;
	private static final float STANDING_CUTOFF_VERTICAL = 0.65f;
	private static final float MAX_DISENGAGMENT_OFFSET = 0.30f;
	private static final float DEFAULT_ARM_DISTANCE = 0.15f;
	private static final float MAX_CORRECTION_STRENGTH_DELTA = 1.0f;

	// counters
	private int leftFramesLocked = 0;
	private int rightFramesLocked = 0;
	private int leftFramesUnlocked = 0;
	private int rightFramesUnlocked = 0;

	// buffer for holding previus frames of data
	private LegTweakBuffer bufferHead = new LegTweakBuffer();
	private boolean bufferInvalid = true;

	public LegTweaks(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public LegTweaks(HumanSkeleton skeleton, LegTweaksConfig config) {
		this.skeleton = skeleton;
		// set all the hyperparameters from the config
		this.config = config;
		updateConfig();
	}

	public Vector3 getLeftFootPosition() {
		return leftFootPosition;
	}

	public void setLeftFootPosition(Vector3 leftFootPosition) {
		this.leftFootPosition = leftFootPosition;
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation;
	}

	public Vector3 getRightFootPosition() {
		return rightFootPosition;
	}

	public void setRightFootPosition(Vector3 rightFootPosition) {
		this.rightFootPosition = rightFootPosition;
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation;
	}

	public Vector3 getLeftKneePosition() {
		return leftKneePosition;
	}

	public void setLeftKneePosition(Vector3 leftKneePosition) {
		this.leftKneePosition = leftKneePosition;
	}

	public Vector3 getRightKneePosition() {
		return rightKneePosition;
	}

	public void setRightKneePosition(Vector3 rightKneePosition) {
		this.rightKneePosition = rightKneePosition;
	}

	public Vector3 getWaistPosition() {
		return waistPosition;
	}

	public void setWaistPosition(Vector3 waistPosition) {
		this.waistPosition = waistPosition;
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

	public void setConfig(LegTweaksConfig config) {
		this.config = config;
		updateConfig();
	}

	public void updateConfig() {
		LegTweaks.updateHyperParameters(config.getCorrectionStrength());
	}

	// update the hyper parameters with the config
	public static void updateHyperParameters(float newStrength) {
		LegTweakBuffer.SKATING_VELOCITY_THRESHOLD = getScaledHyperParameter(
			newStrength,
			LegTweakBuffer.SKATING_VELOCITY_THRESHOLD
		);
		LegTweakBuffer.SKATING_ACCELERATION_THRESHOLD = getScaledHyperParameter(
			newStrength,
			LegTweakBuffer.SKATING_ACCELERATION_THRESHOLD
		);
		currentCorrectionStrength = newStrength;
	}

	public static float getScaledHyperParameter(float newStrength, float currentValue) {
		return (currentValue - (currentCorrectionStrength * MAX_CORRECTION_STRENGTH_DELTA))
			+ (newStrength * MAX_CORRECTION_STRENGTH_DELTA);
	}

	// set the vectors in this object to the vectors in the skeleton
	private void setVectors() {
		// set the positions of the feet and knees to the skeleton's
		// current positions
		waistPosition = skeleton.computedWaistTracker.getPosition();

		leftKneePosition = skeleton.computedLeftKneeTracker.getPosition();
		rightKneePosition = skeleton.computedRightKneeTracker.getPosition();

		leftFootPosition = skeleton.computedLeftFootTracker.getPosition();
		rightFootPosition = skeleton.computedRightFootTracker.getPosition();
		leftFootRotation = skeleton.computedLeftFootTracker.getRotation();
		rightFootRotation = skeleton.computedRightFootTracker.getRotation();

		// get the vector for acceleration of the feet and knees
		// (if feet are not available, fallback to 6 tracker mode)
		if (skeleton.leftFootTracker != null && skeleton.rightFootTracker != null) {
			leftFootAcceleration = skeleton.leftFootTracker.getAcceleration();
			rightFootAcceleration = skeleton.rightFootTracker.getAcceleration();
		} else {
			leftFootAcceleration = Vector3.Companion.getNULL();
			rightFootAcceleration = Vector3.Companion.getNULL();
		}

		if (skeleton.leftLowerLegTracker != null && skeleton.rightLowerLegTracker != null) {
			leftLowerLegAcceleration = skeleton.leftLowerLegTracker.getAcceleration();
			rightLowerLegAcceleration = skeleton.rightLowerLegTracker.getAcceleration();
		} else {
			leftLowerLegAcceleration = Vector3.Companion.getNULL();
			rightLowerLegAcceleration = Vector3.Companion.getNULL();
		}
	}

	// updates the object with the latest data from the skeleton
	private boolean preUpdate() {
		// populate the vectors with the latest data
		setVectors();

		// if not initialized, we need to calculate some values from this frame
		// to be used later (must happen immediately after reset)
		if (!initialized) {
			floorLevel = (leftFootPosition.getY() + rightFootPosition.getY()) / 2f
				+ FLOOR_CALIBRATION_OFFSET;
			waistToFloorDist = waistPosition.getY() - floorLevel;

			// invalidate the buffer since the non-initialized output may be
			// very wrong
			bufferInvalid = true;
			initialized = true;
		}

		// if not enabled do nothing and return false
		if (!enabled)
			return false;

		// if the user is standing start checking for a good time to enable leg
		// tweaks
		active = isStanding();

		// if the buffer is invalid add all the extra info
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

			// if the system is active populate the buffer with corrected floor
			// clip feet positions
			if (active && isStanding()) {
				correctClipping();
				bufferHead.setLeftFootPositionCorrected(leftFootPosition);
				bufferHead.setRightFootPositionCorrected(rightFootPosition);
			}

			bufferInvalid = false;
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
		currentFrame.setCenterOfMass(computeCenterOfMass());

		currentFrame
			.setLeftFloorLevel(
				(floorLevel + (MAX_DYNAMIC_DISPLACEMENT * getLeftFootOffset()))
					- currentDisengagementOffset
			);

		currentFrame
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

		// update the buffer head and compute the current state of the legs
		currentFrame.setParent(bufferHead);
		this.bufferHead = currentFrame;
		this.bufferHead.calculateFootAttributes(active);

		// update the lock duration counters
		updateLockStateCounters();

		return true;
	}

	// tweak the position of the legs based on data from the last frames
	public void tweakLegs() {
		// update the class with the latest data from the skeleton
		// if false is returned something indicated that the legs should not be
		// tweaked
		if (!preUpdate())
			return;

		// push the feet up if needed
		if (floorclipEnabled)
			correctClipping();

		// correct for skating if needed
		if (skatingCorrectionEnabled)
			correctSkating();

		// determine if either leg is in a position to activate or deactivate
		// (use the buffer to get the positions before corrections)
		float leftFootDif = FastMath
			.abs(
				bufferHead
					.getLeftFootPosition()
					.minus(leftFootPosition)
					.getY()
			);

		float rightFootDif = FastMath
			.abs(
				bufferHead
					.getRightFootPosition()
					.minus(rightFootPosition)
					.getY()
			);

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
			leftFootPosition = new Vector3(
				leftFootPosition.getX(),
				bufferHead.getLeftFootPosition().getY(),
				leftFootPosition.getZ()
			);
			leftKneePosition = new Vector3(
				leftKneePosition.getX(),
				bufferHead.getLeftKneePosition().getY(),
				leftKneePosition.getZ()
			);
		}

		if (!rightLegActive) {
			rightFootPosition = new Vector3(
				rightFootPosition.getX(),
				bufferHead.getLeftFootPosition().getY(),
				rightFootPosition.getZ()
			);
			rightKneePosition = new Vector3(
				rightKneePosition.getX(),
				bufferHead.getLeftKneePosition().getY(),
				rightKneePosition.getZ()
			);
		}

		// calculate the correction for the knees
		if (initialized)
			solveLowerBody();

		// populate the corrected data into the current frame
		this.bufferHead.setLeftFootPositionCorrected(leftFootPosition);
		this.bufferHead.setRightFootPositionCorrected(rightFootPosition);
		this.bufferHead.setLeftKneePositionCorrected(leftKneePosition);
		this.bufferHead.setRightKneePositionCorrected(rightKneePosition);
		this.bufferHead.setWaistPositionCorrected(waistPosition);
	}

	// returns true if the foot is clipped and false if it is not
	public boolean isClipped(float leftOffset, float rightOffset) {
		return (leftFootPosition.getY() < floorLevel + leftOffset
			|| rightFootPosition.getY() < floorLevel + rightOffset);
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
		if (!isClipped(leftOffset, rightOffset) || !enabled)
			return;

		// move the feet to their new positions
		if (
			leftFootPosition.getY()
				< (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * leftOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (MAX_DYNAMIC_DISPLACEMENT * leftOffset)
						- leftFootPosition.getY()
						- currentDisengagementOffset
				);

			leftFootPosition = new Vector3(
				leftFootPosition.getX(),
				leftFootPosition.getY() + displacement,
				leftFootPosition.getZ()
			);
			leftKneePosition = new Vector3(
				leftKneePosition.getX(),
				leftKneePosition.getY() + displacement,
				leftKneePosition.getZ()
			);
			avgOffset += displacement;
		}

		if (
			rightFootPosition.getY()
				< (floorLevel + (MAX_DYNAMIC_DISPLACEMENT * rightOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (MAX_DYNAMIC_DISPLACEMENT * rightOffset)
						- rightFootPosition.getY()
						- currentDisengagementOffset
				);

			rightFootPosition = new Vector3(
				rightFootPosition.getX(),
				rightFootPosition.getY() + displacement,
				rightFootPosition.getZ()
			);
			rightKneePosition = new Vector3(
				rightKneePosition.getX(),
				rightKneePosition.getY() + displacement,
				rightKneePosition.getZ()
			);
			avgOffset += displacement;
		}

		waistPosition = new Vector3(
			waistPosition.getX(),
			waistPosition.getY() + ((avgOffset / 2) * WAIST_PUSH_WEIGHT),
			waistPosition.getZ()
		);
	}

	// based on the data from the last frame compute a new position that reduces
	// ice skating
	private void correctSkating() {
		// for either foot that is locked get its position (x and z only we let
		// y move freely) and set it to be there
		if (bufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			leftFootPosition = new Vector3(
				bufferHead
					.getParent()
					.getLeftFootPositionCorrected()
					.getX(),
				leftFootPosition.getY(),
				bufferHead
					.getParent()
					.getLeftFootPositionCorrected()
					.getZ()
			);
		}

		if (bufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			rightFootPosition = new Vector3(
				bufferHead
					.getParent()
					.getRightFootPositionCorrected()
					.getX(),
				rightFootPosition.getY(),
				bufferHead
					.getParent()
					.getRightFootPositionCorrected()
					.getZ()
			);
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
			Vector3 leftFootDif = leftFootPosition
				.minus(bufferHead.getParent().getLeftFootPositionCorrected());
			leftFootDif = new Vector3(leftFootDif.getX(), 0f, leftFootDif.getZ());

			if (leftFootDif.len() > NEARLY_ZERO) {
				float leftY = leftFootPosition.getY();

				Vector3 temp = bufferHead.getParent().getLeftFootPositionCorrected();
				Vector3 velocity = bufferHead.getLeftFootVelocity();

				// first add the difference from the last frame to this frame
				temp = temp
					.minus(
						bufferHead
							.getParent()
							.getLeftFootPosition()
							.minus(bufferHead.getLeftFootPosition())
					);

				leftFootPosition = new Vector3(temp.getX(), leftY, temp.getZ());

				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				// calculate the correction weight.
				// it is also right here where the constant correction is
				// applied
				float weight = calculateCorrectionWeight(
					leftFootPosition,
					bufferHead.getParent().getLeftFootPositionCorrected()
				);

				float leftX = leftFootPosition.getX();
				float leftZ = leftFootPosition.getZ();
				if (velocity.getX() * leftFootDif.getX() > 0) {
					leftX += (velocity.getX() * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.getX() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.getX() * leftFootDif.getX() < 0) {
					leftX -= (velocity.getX() * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.getX() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				if (velocity.getZ() * leftFootDif.getZ() > 0) {
					leftZ += (velocity.getZ() * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.getZ() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.getZ() * leftFootDif.getZ() < 0) {
					leftZ -= (velocity.getZ() * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.getZ() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				// if the foot overshot the target, move it back to the target
				if (
					checkOverShoot(
						this.bufferHead.getLeftFootPosition().getX(),
						this.bufferHead.getParent().getLeftFootPositionCorrected().getX(),
						leftX
					)
				) {
					leftX = bufferHead.getLeftFootPosition().getX();
				}

				if (
					checkOverShoot(
						this.bufferHead.getLeftFootPosition().getZ(),
						this.bufferHead.getParent().getLeftFootPositionCorrected().getZ(),
						leftFootPosition.getZ()
					)
				) {
					leftZ = bufferHead.getLeftFootPosition().getZ();
				}

				leftFootPosition = new Vector3(leftX, leftFootPosition.getY(), leftZ);
			}
		}
	}

	private void correctUnlockedRightFootTracker() {
		if (bufferHead.getRightLegState() == LegTweakBuffer.UNLOCKED) {
			Vector3 rightFootDif = rightFootPosition
				.minus(bufferHead.getParent().getRightFootPositionCorrected());
			rightFootDif = new Vector3(rightFootDif.getX(), 0f, rightFootDif.getZ());

			if (rightFootDif.len() > NEARLY_ZERO) {
				float rightY = rightFootPosition.getY();
				Vector3 temp = bufferHead
					.getParent()
					.getRightFootPositionCorrected();
				Vector3 velocity = bufferHead.getRightFootVelocity();

				// first add the difference from the last frame to this frame
				temp = temp
					.minus(
						bufferHead
							.getParent()
							.getRightFootPosition()
							.minus(bufferHead.getRightFootPosition())
					);

				rightFootPosition = new Vector3(temp.getX(), rightY, temp.getZ());

				// if velocity and dif are pointing in the same direction,
				// add a small amount of velocity to the dif
				// else subtract a small amount of velocity from the dif
				// calculate the correction weight
				float weight = calculateCorrectionWeight(
					rightFootPosition,
					bufferHead.getParent().getRightFootPositionCorrected()
				);

				float rightX = rightFootPosition.getX();
				float rightZ = rightFootPosition.getZ();
				if (velocity.getX() * rightFootDif.getX() > 0) {
					rightX += (velocity.getX() * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.getX() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.getX() * rightFootDif.getX() < 0) {
					rightX -= (velocity.getX() * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.getX() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				if (velocity.getZ() * rightFootDif.getZ() > 0) {
					rightZ += (velocity.getZ() * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.getZ() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.getZ() * rightFootDif.getZ() < 0) {
					rightZ -= (velocity.getZ() * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.getZ() > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				// if the foot overshot the target, move it back to the target
				if (
					checkOverShoot(
						this.bufferHead.getRightFootPosition().getX(),
						this.bufferHead.getParent().getRightFootPositionCorrected().getX(),
						rightX
					)
				) {
					rightX = bufferHead.getRightFootPosition().getX();
				}

				if (
					checkOverShoot(
						this.bufferHead.getRightFootPosition().getZ(),
						this.bufferHead.getParent().getRightFootPositionCorrected().getZ(),
						rightZ
					)
				) {
					rightZ += bufferHead.getRightFootPosition().getZ();
				}

				rightFootPosition = new Vector3(
					rightX,
					rightFootPosition.getY(),
					rightZ
				);
			}
		}
	}

	// returns true if it is likely the user is standing
	public boolean isStanding() {
		// if the waist is below the vertical cutoff, user is not standing
		float cutoff = floorLevel
			+ waistToFloorDist
			- (waistToFloorDist * STANDING_CUTOFF_VERTICAL);

		if (waistPosition.getY() < cutoff) {
			currentDisengagementOffset = (1 - waistPosition.getY() / cutoff)
				* MAX_DISENGAGMENT_OFFSET;

			return false;
		}

		currentDisengagementOffset = 0f;

		return true;
	}

	// move the knees in to a position that is closer to the truth
	private void solveLowerBody() {
		// calculate the left and right waist nodes in standing space
		Vector3 leftWaist = waistPosition;
		Vector3 rightWaist = waistPosition;

		Vector3 tempLeft;
		Vector3 tempRight;

		// before moveing the knees back closer to the waist nodes offset them
		// the same amount the foot trackers where offset
		float leftXDif = leftFootPosition.getX() - bufferHead.getLeftFootPosition().getX();
		float rightXDif = rightFootPosition.getX() - bufferHead.getRightFootPosition().getX();
		float leftZDif = leftFootPosition.getZ() - bufferHead.getLeftFootPosition().getZ();
		float rightZDif = rightFootPosition.getZ() - bufferHead.getRightFootPosition().getZ();

		leftKneePosition.getX() += leftXDif * KNEE_LATERAL_WEIGHT;
		leftKneePosition.getZ() += leftZDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.getX() += rightXDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.getZ() += rightZDif * KNEE_LATERAL_WEIGHT;

		// calculate the bone distances
		float leftKneeWaist = bufferHead.getLeftKneePosition().minus(leftWaist).len();
		float rightKneeWaist = bufferHead.getRightKneePosition().minus(rightWaist).len();

		float leftKneeWaistNew = leftKneePosition.minus(leftWaist).len();
		float rightKneeWaistNew = rightKneePosition.minus(rightWaist).len();
		float leftKneeOffset = leftKneeWaistNew - leftKneeWaist;
		float rightKneeOffset = rightKneeWaistNew - rightKneeWaist;

		// get the vector from the waist to the knee
		Vector3 leftKneeVector = leftKneePosition
			.minus(leftWaist)
			.unit()
			.times(leftKneeOffset * KNEE_CORRECTION_WEIGHT);

		Vector3 rightKneeVector = rightKneePosition
			.minus(rightWaist)
			.unit()
			.times(rightKneeOffset * KNEE_CORRECTION_WEIGHT);

		// correct the knees
		tempLeft = leftKneePosition.minus(leftKneeVector);
		tempRight = rightKneePosition.minus(rightKneeVector);
		leftKneePosition = tempLeft;
		rightKneePosition = tempRight;
	}

	private float getLeftFootOffset() {
		float offset = computeUnitVector(this.leftFootRotation).getY();
		return FastMath.clamp(offset, 0, DYNAMIC_DISPLACEMENT_CUTOFF);
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).getY();
		return FastMath.clamp(offset, 0, DYNAMIC_DISPLACEMENT_CUTOFF);
	}

	// calculate the weight of foot correction
	private float calculateCorrectionWeight(
		Vector3 foot,
		Vector3 footCorrected
	) {
		Vector3 footDif = foot.minus(footCorrected).setY(0);

		if (footDif.len() < MIN_ACCEPTABLE_ERROR) {
			return CORRECTION_WEIGHT_MIN;
		} else if (footDif.len() > MAX_ACCEPTABLE_ERROR) {
			return CORRECTION_WEIGHT_MAX;
		}

		return CORRECTION_WEIGHT_MIN
			+ (footDif.len() - MIN_ACCEPTABLE_ERROR)
				/ (MAX_ACCEPTABLE_ERROR - MIN_ACCEPTABLE_ERROR)
				* (CORRECTION_WEIGHT_MAX - CORRECTION_WEIGHT_MIN);
	}

	// calculate the center of mass of the user for the current frame
	// returns a vector representing the center of mass position
	private Vector3 computeCenterOfMass() {
		// check if arm data is available
		boolean armsAvailable = skeleton.hasLeftArmTracker
			&& skeleton.hasRightArmTracker;

		Vector3 centerOfMass = new Vector3();

		// compute the center of mass of smaller body parts and then sum them up
		// with their respective weights
		Vector3 head = skeleton.headNode.getWorldTransform().getTranslation();
		Vector3 chest = skeleton.chestNode.getWorldTransform().getTranslation();
		Vector3 waist = skeleton.waistNode.getWorldTransform().getTranslation();
		Vector3 leftCalf = getCenterOfJoint(skeleton.leftAnkleNode, skeleton.leftKneeNode);
		Vector3 rightCalf = getCenterOfJoint(skeleton.rightAnkleNode, skeleton.rightKneeNode);
		Vector3 leftThigh = getCenterOfJoint(skeleton.leftKneeNode, skeleton.leftHipNode);
		Vector3 rightThigh = getCenterOfJoint(skeleton.rightKneeNode, skeleton.rightHipNode);
		centerOfMass = centerOfMass.plus(head.times(HEAD_MASS));
		centerOfMass = centerOfMass.plus(chest.times(CHEST_MASS));
		centerOfMass = centerOfMass.plus(waist.times(WAIST_MASS));
		centerOfMass = centerOfMass.plus(leftCalf.times(CALF_MASS));
		centerOfMass = centerOfMass.plus(rightCalf.times(CALF_MASS));
		centerOfMass = centerOfMass.plus(leftThigh.times(THIGH_MASS));
		centerOfMass = centerOfMass.plus(rightThigh.times(THIGH_MASS));

		if (armsAvailable) {
			Vector3 leftUpperArm = getCenterOfJoint(
				skeleton.leftElbowNode,
				skeleton.leftShoulderTailNode
			);
			Vector3 rightUpperArm = getCenterOfJoint(
				skeleton.rightElbowNode,
				skeleton.rightShoulderTailNode
			);
			Vector3 leftForearm = getCenterOfJoint(skeleton.leftElbowNode, skeleton.leftHandNode);
			Vector3 rightForearm = getCenterOfJoint(
				skeleton.rightElbowNode,
				skeleton.rightHandNode
			);
			centerOfMass = centerOfMass.plus(leftUpperArm.times(UPPER_ARM_MASS));
			centerOfMass = centerOfMass.plus(rightUpperArm.times(UPPER_ARM_MASS));
			centerOfMass = centerOfMass.plus(leftForearm.times(FOREARM_MASS));
			centerOfMass = centerOfMass.plus(rightForearm.times(FOREARM_MASS));
		} else {
			// if the arms are not avaliable put them slightly in front
			// of the chest.
			Vector3 chestUnitVector = computeUnitVector(
				skeleton.chestNode.getWorldTransform().getRotation()
			);
			Vector3 armLocation = chest.plus(chestUnitVector.times(DEFAULT_ARM_DISTANCE));
			centerOfMass = centerOfMass.plus(armLocation.times(UPPER_ARM_MASS * 2.0f));
			centerOfMass = centerOfMass.plus(armLocation.times(FOREARM_MASS * 2.0f));
		}

		// finally translate in to tracker space
		centerOfMass = waistPosition
			.plus(
				centerOfMass.minus(skeleton.trackerHipNode.getWorldTransform().getTranslation())
			);

		return centerOfMass;
	}

	// get the center of two joints
	private Vector3 getCenterOfJoint(TransformNode node1, TransformNode node2) {
		return node1
			.getWorldTransform()
			.getTranslation()
			.plus(node2.getWorldTransform().getTranslation())
			.times(0.5f);
	}

	// get the amount of the constant correction to apply.
	private float getConstantCorrectionQuantityLeft() {
		if (leftFramesUnlocked >= CONTINUOUS_CORRECTION_WARMUP)
			return CONTINUOUS_CORRECTION_DIST;

		return CONTINUOUS_CORRECTION_DIST
			* ((float) leftFramesUnlocked / CONTINUOUS_CORRECTION_WARMUP);
	}

	private float getConstantCorrectionQuantityRight() {
		if (rightFramesUnlocked >= CONTINUOUS_CORRECTION_WARMUP)
			return CONTINUOUS_CORRECTION_DIST;

		return CONTINUOUS_CORRECTION_DIST
			* ((float) rightFramesUnlocked / CONTINUOUS_CORRECTION_WARMUP);
	}

	// update counters for the lock state of the feet
	private void updateLockStateCounters() {
		if (bufferHead.getLeftLegState() == LegTweakBuffer.LOCKED) {
			leftFramesUnlocked = 0;
			leftFramesLocked++;
		} else {
			leftFramesLocked = 0;
			leftFramesUnlocked++;
		}

		if (bufferHead.getRightLegState() == LegTweakBuffer.LOCKED) {
			rightFramesUnlocked = 0;
			rightFramesLocked++;
		} else {
			rightFramesLocked = 0;
			rightFramesUnlocked++;
		}
	}

	// check if the difference between two floats flipped after correction
	private boolean checkOverShoot(float trueVal, float valBefore, float valAfter) {
		return (trueVal - valBefore) * (trueVal - valAfter) < 0;
	}

	// get the unit vector of the given rotation
	private Vector3 computeUnitVector(Quaternion quaternion) {
		return quaternion.toMatrix().getZ().unit();
	}
}
