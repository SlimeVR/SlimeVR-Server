package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.config.LegTweaksConfig;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles;


public class LegTweaks {
	/**
	 * here is an explanation of each parameter that may need explaining
	 * STANDING_CUTOFF_VERTICAL is the percentage the waist has to be below its
	 * position at calibration to register as the user not standing
	 * MAX_DISENGAGEMENT_OFFSET is how much the floor will be shifted to allow
	 * an offset to happen smoothly DYNAMIC_DISPLACEMENT_CUTOFF is the percent
	 * of downwards rotation that can contribute to dynamic displacement
	 * MAX_DYNAMIC_DISPLACEMENT is the max amount the floor will be moved up to
	 * account for the foot rotating downward and needing to be put higher to
	 * avoid clipping in the game world MIN_ACCEPTABLE_ERROR and
	 * MAX_ACCEPTABLE_ERROR Defines the distance where CORRECTION_WEIGHT_MIN and
	 * CORRECTION_WEIGHT_MAX are calculating a percent of velocity to correct
	 * rather than using the min or max FLOOR_CALIBRATION_OFFSET is the amount
	 * the floor plane is shifted up. This can help the feet from floating
	 * slightly above the ground
	 */

	// hyperparameters (clip correction)
	static float DYNAMIC_DISPLACEMENT_CUTOFF = 1.0f;
	private static final float FLOOR_CALIBRATION_OFFSET = 0.015f;

	// hyperparameters (skating correction)
	private static final float MIN_ACCEPTABLE_ERROR = 0.01f;
	private static final float MAX_ACCEPTABLE_ERROR = 0.05f;
	private static final float CORRECTION_WEIGHT_MIN = 0.55f;
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

	// hyperparameters (rotation correction)
	private static final float ROTATION_CORRECTION_VERTICAL = 0.1f;
	private static final float MAXIMUM_CORRECTION_ANGLE = 0.4f;
	private static final float MAXIMUM_CORRECTION_ANGLE_DELTA = 0.7f;
	private static final float MAXIMUM_TOE_DOWN_ANGLE = 0.8f;
	private static final float TOE_SNAP_COOLDOWN = 3.0f;

	// hyperparameters (misc)
	static final float NEARLY_ZERO = 0.001f;
	private static final float STANDING_CUTOFF_VERTICAL = 0.65f;
	private static final float MAX_DISENGAGEMENT_OFFSET = 0.30f;
	private static final float DEFAULT_ARM_DISTANCE = 0.15f;
	private static final float MAX_CORRECTION_STRENGTH_DELTA = 1.0f;

	// state variables
	private float floorLevel;
	private float waistToFloorDist;
	private float currentDisengagementOffset = 0.0f;
	private float footLength = 0.0f;
	private static float currentCorrectionStrength = 0.3f; // default value
	private boolean initialized = true;
	private boolean enabled = true; // master switch
	private boolean floorclipEnabled = false;
	private boolean skatingCorrectionEnabled = false;
	private boolean toeSnap = false;
	private boolean footPlant = false;
	private boolean active = false;
	private boolean rightLegActive = false;
	private boolean leftLegActive = false;
	private int leftFramesLocked = 0;
	private int rightFramesLocked = 0;
	private int leftFramesUnlocked = 0;
	private int rightFramesUnlocked = 0;
	private float leftToeAngle = 0.0f;
	private boolean leftToeTouched = false;
	private float rightToeAngle = 0.0f;
	private boolean rightToeTouched = false;

	// skeleton and config
	private HumanSkeleton skeleton;
	private LegTweaksConfig config;

	// leg data
	private Vector3f leftFootPosition = new Vector3f();
	private Vector3f rightFootPosition = new Vector3f();
	private Vector3f leftKneePosition = new Vector3f();
	private Vector3f rightKneePosition = new Vector3f();
	private Vector3f waistPosition = new Vector3f();
	private Quaternion leftFootRotation = new Quaternion();
	private Quaternion rightFootRotation = new Quaternion();

	private Vector3f leftFootAcceleration = new Vector3f();
	private Vector3f rightFootAcceleration = new Vector3f();
	private Vector3f leftLowerLegAcceleration = new Vector3f();
	private Vector3f rightLowerLegAcceleration = new Vector3f();

	// knee placeholder
	private Vector3f leftKneePlaceholder = new Vector3f();
	private Vector3f rightKneePlaceholder = new Vector3f();
	private boolean kneesActive = false;

	// buffer for holding previous frames of data
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

	public void setToeSnap(boolean val) {
		this.toeSnap = val;
	}

	public void setFootPlant(boolean val) {
		this.footPlant = val;
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

	public boolean getToeSnap() {
		return this.toeSnap;
	}

	public boolean getFootPlant() {
		return this.footPlant;
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

		floorclipEnabled = skeleton.humanPoseManager.getToggle(SkeletonConfigToggles.FLOOR_CLIP);
		skatingCorrectionEnabled = skeleton.humanPoseManager
			.getToggle(SkeletonConfigToggles.SKATING_CORRECTION);
		toeSnap = skeleton.humanPoseManager.getToggle(SkeletonConfigToggles.TOE_SNAP);
		footPlant = skeleton.humanPoseManager.getToggle(SkeletonConfigToggles.FOOT_PLANT);
	}

	// update the hyperparameters with the config
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
		// set the positions of the feet and knees to the skeletons current
		// positions
		if (skeleton.computedLeftKneeTracker != null && skeleton.computedRightKneeTracker != null) {
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

			// invalidate the buffer since the non-initialized output may be
			// very wrong
			bufferInvalid = true;
			initialized = true;
		}

		// update the foot length
		footLength = skeleton.leftFootNode.localTransform.getTranslation().length();

		// if not enabled, do nothing and return false
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

			// if the system is active, populate the buffer with corrected floor
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
				(floorLevel + (footLength * getLeftFootOffset()))
					- currentDisengagementOffset
			);

		currentFrame
			.setRightFloorLevel(
				(floorLevel + (footLength * getRightFootOffset()))
					- currentDisengagementOffset
			);

		// put the acceleration vector that is applicable to the tracker
		// quantity in the buffer
		// (if feet are not available, fall back to 6 tracker mode)
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

		// correct foot rotation's
		correctFootRotations();

		// push the feet up if needed
		if (floorclipEnabled)
			correctClipping();

		// correct for skating if needed
		if (skatingCorrectionEnabled)
			correctSkating();

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
		if (kneesActive && initialized)
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
		if (!isClipped(leftOffset, rightOffset) || !enabled)
			return;

		// move the feet to their new positions
		if (
			leftFootPosition.y
				< (floorLevel + (footLength * leftOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (footLength * leftOffset)
						- leftFootPosition.y
						- currentDisengagementOffset
				);

			leftFootPosition.y += displacement;
			leftKneePosition.y += displacement;
			avgOffset += displacement;
		}

		if (
			rightFootPosition.y
				< (floorLevel + (footLength * rightOffset))
					- currentDisengagementOffset
		) {
			float displacement = Math
				.abs(
					floorLevel
						+ (footLength * rightOffset)
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
				// calculate the correction weight.
				// it is also right here where the constant correction is
				// applied
				float weight = calculateCorrectionWeight(
					leftFootPosition,
					bufferHead.getParent().getLeftFootPositionCorrected(null)
				);

				if (velocity.x * leftFootDif.x > 0) {
					leftFootPosition.x += (velocity.x * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.x > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.x * leftFootDif.x < 0) {
					leftFootPosition.x -= (velocity.x * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.x > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				if (velocity.z * leftFootDif.z > 0) {
					leftFootPosition.z += (velocity.z * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.z > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.z * leftFootDif.z < 0) {
					leftFootPosition.z -= (velocity.z * weight)
						+ (getConstantCorrectionQuantityLeft()
							* (velocity.z > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
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
					rightFootPosition.x += (velocity.x * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.x > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.x * rightFootDif.x < 0) {
					rightFootPosition.x -= (velocity.x * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.x > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				}

				if (velocity.z * rightFootDif.z > 0) {
					rightFootPosition.z += (velocity.z * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.z > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
				} else if (velocity.z * rightFootDif.z < 0) {
					rightFootPosition.z -= (velocity.z * weight)
						+ (getConstantCorrectionQuantityRight()
							* (velocity.z > 0 ? 1 : -1)
							/ bufferHead.getTimeDelta());
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

	// correct the rotations of the feet
	// this is done by planting the foot better and by snapping the toes to the
	// ground
	private void correctFootRotations() {
		// null check's
		if (bufferHead == null || bufferHead.getParent() == null)
			return;

		// if there is a foot tracker for a foot don't correct it
		if (skeleton.leftFootTracker != null || skeleton.rightFootTracker != null)
			return;

		// get the foot positions
		Vector3f leftFootPos = bufferHead.getLeftFootPosition(null);
		Vector3f rightFootPos = bufferHead.getRightFootPosition(null);
		Quaternion leftFootRotation = bufferHead.getLeftFootRotation(null);
		Quaternion rightFootRotation = bufferHead.getRightFootRotation(null);

		// between maximum correction angle and maximum correction angle delta
		// the values are interpolated
		float kneeAngleL = getXZAmount(leftFootPos, bufferHead.getLeftKneePosition(null));
		float kneeAngleR = getXZAmount(rightFootPos, bufferHead.getRightKneePosition(null));

		float masterWeightL = getMasterWeight(kneeAngleL);
		float masterWeightR = getMasterWeight(kneeAngleR);

		// corrects rotations when planted firmly on the ground
		if (footPlant) {
			// prepare the weight vars for this correction step
			float weightL = 0.0f;
			float weightR = 0.0f;

			// the further from the ground the foot is, the less weight it
			// should have
			weightL = getFootPlantWeight(leftFootPos);
			weightR = getFootPlantWeight(rightFootPos);

			// perform the correction
			leftFootRotation
				.set(
					leftFootRotation
						.slerp(
							leftFootRotation,
							isolateYaw(leftFootRotation),
							weightL * masterWeightL
						)
				);

			rightFootRotation
				.set(
					rightFootRotation
						.slerp(
							rightFootRotation,
							isolateYaw(rightFootRotation),
							weightR * masterWeightR
						)
				);
		}

		// corrects rotations when the foot is in the air by rotating the foot
		// down so that the toes are touching
		if (toeSnap) {
			// this correction step has its own weight vars
			float weightL = 0.0f;
			float weightR = 0.0f;

			// first compute the angle of the foot
			float angleL = getToeSnapAngle(leftFootPos);
			float angleR = getToeSnapAngle(rightFootPos);

			// then compute the weight of the correction
			weightL = getToeSnapWeight(leftFootPos, angleL);
			weightR = getToeSnapWeight(rightFootPos, angleR);

			// depending on the state variables, the correction weights should
			// be clamped
			if (!leftToeTouched) {
				weightL = Math.min(weightL, leftToeAngle);
			}
			if (!rightToeTouched) {
				weightR = Math.min(weightR, rightToeAngle);
			}

			// then slerp the rotation to the new rotation based on the weight
			leftFootRotation
				.slerp(
					leftFootRotation,
					replacePitch(leftFootRotation, -angleL),
					weightL * masterWeightL
				);
			rightFootRotation
				.slerp(
					rightFootRotation,
					replacePitch(rightFootRotation, -angleR),
					weightR * masterWeightR
				);

			// update state variables regarding toe snap
			if (leftFootPos.y - floorLevel > footLength * MAXIMUM_TOE_DOWN_ANGLE) {
				leftToeTouched = false;
				leftToeAngle = weightL;
			} else if (leftFootPos.y - floorLevel < 0.0f) {
				leftToeTouched = true;
				leftToeAngle = 1.0f;
			}
			if (rightFootPos.y - floorLevel > footLength * MAXIMUM_TOE_DOWN_ANGLE) {
				rightToeTouched = false;
				rightToeAngle = weightR;
			} else if (rightFootPos.y - floorLevel < 0.0f) {
				rightToeTouched = true;
				rightToeAngle = 1.0f;
			}
		}

		// update the foot rotations in the buffer
		bufferHead.setLeftFootRotationCorrected(leftFootRotation);
		bufferHead.setRightFootRotationCorrected(rightFootRotation);

		// finally update the skeletons rotations with the new rotations
		skeleton.computedLeftFootTracker.rotation.set(leftFootRotation);
		skeleton.computedRightFootTracker.rotation.set(rightFootRotation);
	}

	// returns the length of the xz components of the normalized difference
	// between two vectors
	public float getXZAmount(Vector3f vec1, Vector3f vec2) {
		return vec1
			.subtract(vec2)
			.normalizeLocal()
			.setY(0.0f)
			.length();
	}

	// returns a float between 0 and 1 that represents the master weight for
	// foot rotation correciton
	private float getMasterWeight(float kneeAngle) {
		float masterWeight = (kneeAngle > MAXIMUM_CORRECTION_ANGLE
			&& kneeAngle < MAXIMUM_CORRECTION_ANGLE_DELTA)
				? 1.0f
					- ((kneeAngle - MAXIMUM_CORRECTION_ANGLE)
						/ (MAXIMUM_CORRECTION_ANGLE_DELTA - MAXIMUM_CORRECTION_ANGLE))
				: 0.0f;
		return (kneeAngle < MAXIMUM_CORRECTION_ANGLE) ? 1.0f : masterWeight;
	}

	// return the weight of the correction for toe snap
	private float getToeSnapWeight(Vector3f footPos, float angle) {
		// then compute the weight of the correction
		float weight = ((footPos.y - floorLevel) > footLength * TOE_SNAP_COOLDOWN)
			? 0.0f
			: 1.0f
				- ((footPos.y - floorLevel - footLength)
					/ (footLength * (TOE_SNAP_COOLDOWN - 1.0f)));
		return FastMath.clamp(weight, 0.0f, 1.0f);
	}

	// returns the angle of the foot for toe snap
	private float getToeSnapAngle(Vector3f footPos) {
		float angle = FastMath.clamp(footPos.y - floorLevel, 0.0f, footLength);
		return (angle > footLength * MAXIMUM_TOE_DOWN_ANGLE)
			? FastMath.asin((footLength * MAXIMUM_TOE_DOWN_ANGLE) / footLength)
			: FastMath.asin((angle / footLength));
	}

	// returns the weight for floor plant
	private float getFootPlantWeight(Vector3f footPos) {
		float weight = (footPos.y - floorLevel > ROTATION_CORRECTION_VERTICAL)
			? 0.0f
			: 1.0f - ((footPos.y - floorLevel) / ROTATION_CORRECTION_VERTICAL);
		return FastMath.clamp(weight, 0.0f, 1.0f);
	}

	// returns true if it is likely the user is standing
	public boolean isStanding() {
		// if the waist is below the vertical cutoff, user is not standing
		float cutoff = floorLevel
			+ waistToFloorDist
			- (waistToFloorDist * STANDING_CUTOFF_VERTICAL);

		if (waistPosition.y < cutoff) {
			currentDisengagementOffset = (1 - waistPosition.y / cutoff)
				* MAX_DISENGAGEMENT_OFFSET;

			return false;
		}

		currentDisengagementOffset = 0f;

		return true;
	}

	// move the knees in to a position that is closer to the truth
	private void solveLowerBody() {
		// calculate the left and right waist nodes in standing space
		Vector3f leftWaist = waistPosition;
		Vector3f rightWaist = waistPosition;

		Vector3f tempLeft;
		Vector3f tempRight;

		// before moving the knees back closer to the waist nodes, offset them
		// the same amount the foot trackers where offset
		float leftXDif = leftFootPosition.x - bufferHead.getLeftFootPosition(null).x;
		float rightXDif = rightFootPosition.x - bufferHead.getRightFootPosition(null).x;
		float leftZDif = leftFootPosition.z - bufferHead.getLeftFootPosition(null).z;
		float rightZDif = rightFootPosition.z - bufferHead.getRightFootPosition(null).z;

		leftKneePosition.x += leftXDif * KNEE_LATERAL_WEIGHT;
		leftKneePosition.z += leftZDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.x += rightXDif * KNEE_LATERAL_WEIGHT;
		rightKneePosition.z += rightZDif * KNEE_LATERAL_WEIGHT;

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
		return FastMath.clamp(offset, 0, DYNAMIC_DISPLACEMENT_CUTOFF);
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		return FastMath.clamp(offset, 0, DYNAMIC_DISPLACEMENT_CUTOFF);
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

	// calculate the center of mass of the user for the current frame
	// returns a vector representing the center of mass position
	private Vector3f computeCenterOfMass() {
		// perform a check to see if the needed data is available
		if (
			skeleton.headNode == null
				|| skeleton.chestNode == null
				|| skeleton.waistNode == null
				|| skeleton.leftFootNode == null
				|| skeleton.rightFootNode == null
				|| skeleton.leftKneeNode == null
				|| skeleton.rightKneeNode == null
				|| skeleton.leftHipNode == null
				|| skeleton.rightHipNode == null
		) {
			return null;
		}

		// check if arm data is available
		boolean armsAvailable = skeleton.hasLeftArmTracker
			&& skeleton.hasRightArmTracker;

		Vector3f centerOfMass = new Vector3f();

		// compute the center of mass of smaller body parts and then sum them up
		// with their respective weights
		Vector3f head = skeleton.headNode.worldTransform.getTranslation();
		Vector3f chest = skeleton.chestNode.worldTransform.getTranslation();
		Vector3f waist = skeleton.waistNode.worldTransform.getTranslation();
		Vector3f leftCalf = getCenterOfJoint(skeleton.leftAnkleNode, skeleton.leftKneeNode);
		Vector3f rightCalf = getCenterOfJoint(skeleton.rightAnkleNode, skeleton.rightKneeNode);
		Vector3f leftThigh = getCenterOfJoint(skeleton.leftKneeNode, skeleton.leftHipNode);
		Vector3f rightThigh = getCenterOfJoint(skeleton.rightKneeNode, skeleton.rightHipNode);
		centerOfMass = centerOfMass.add(head.mult(HEAD_MASS));
		centerOfMass = centerOfMass.add(chest.mult(CHEST_MASS));
		centerOfMass = centerOfMass.add(waist.mult(WAIST_MASS));
		centerOfMass = centerOfMass.add(leftCalf.mult(CALF_MASS));
		centerOfMass = centerOfMass.add(rightCalf.mult(CALF_MASS));
		centerOfMass = centerOfMass.add(leftThigh.mult(THIGH_MASS));
		centerOfMass = centerOfMass.add(rightThigh.mult(THIGH_MASS));

		if (armsAvailable) {
			Vector3f leftUpperArm = getCenterOfJoint(
				skeleton.leftElbowNode,
				skeleton.leftShoulderTailNode
			);
			Vector3f rightUpperArm = getCenterOfJoint(
				skeleton.rightElbowNode,
				skeleton.rightShoulderTailNode
			);
			Vector3f leftForearm = getCenterOfJoint(skeleton.leftElbowNode, skeleton.leftHandNode);
			Vector3f rightForearm = getCenterOfJoint(
				skeleton.rightElbowNode,
				skeleton.rightHandNode
			);
			centerOfMass = centerOfMass.add(leftUpperArm.mult(UPPER_ARM_MASS));
			centerOfMass = centerOfMass.add(rightUpperArm.mult(UPPER_ARM_MASS));
			centerOfMass = centerOfMass.add(leftForearm.mult(FOREARM_MASS));
			centerOfMass = centerOfMass.add(rightForearm.mult(FOREARM_MASS));
		} else {
			// if the arms are not available put them slightly in front
			// of the chest.
			Vector3f chestUnitVector = computeUnitVector(
				skeleton.chestNode.worldTransform.getRotation()
			);
			Vector3f armLocation = chest.add(chestUnitVector.mult(DEFAULT_ARM_DISTANCE));
			centerOfMass = centerOfMass.add(armLocation.mult(UPPER_ARM_MASS * 2.0f));
			centerOfMass = centerOfMass.add(armLocation.mult(FOREARM_MASS * 2.0f));
		}

		// finally translate in to tracker space
		centerOfMass = waistPosition
			.add(
				centerOfMass.subtract(skeleton.trackerHipNode.worldTransform.getTranslation(null))
			);

		return centerOfMass;
	}

	// get the center of two joints
	private Vector3f getCenterOfJoint(TransformNode node1, TransformNode node2) {
		return node1.worldTransform
			.getTranslation(null)
			.add(node2.worldTransform.getTranslation(null))
			.mult(0.5f);
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

	// remove the x and z components of the given quaternion
	private Quaternion isolateYaw(Quaternion quaternion) {
		return new Quaternion(
			0,
			quaternion.getY(),
			0,
			quaternion.getW()
		);
	}

	// return a quaternion that has been rotated by the new pitch amount
	private Quaternion replacePitch(Quaternion quaternion, float newPitch) {
		// first get the axis of the current pitch
		Vector3f currentPitchAxis = quaternion.getRotationColumn(0).normalize();

		// then get the current pitch
		float currentPitch = (float) Math.asin(currentPitchAxis.y);

		// then add the new pitch
		Quaternion newQuat = new Quaternion();
		newQuat.fromAngleAxis(newPitch + currentPitch, currentPitchAxis);

		return newQuat.mult(quaternion);
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
