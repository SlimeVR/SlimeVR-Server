package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


// class that holds data related to the state and other variuse attributes of the legs
// such as the position of the foot, knee, and waist, after and before correction,
// the velocity of the foot and the computed state of the feet at that frame.
// mainly calculates the state of the legs per frame using these rules:
// The conditions for an unlock are as follows:
// 1. the foot is to far from its correct position
// 2. a velocity higher than a threashold is achived
// 3. a large acceleration is applied to the foot
// 4. angular velocity of the foot goes higher than a threashold
// The conditions for a lock are the opposite of the above
// but require a lower value for all of the above conditions


public class LegTweakBuffer {

	public static final int STATE_UNKNOWN = 0; // fall back state
	public static final int LOCKED = 1;
	public static final int UNLOCKED = 2;
	public static final int FOOT_ACCEL = 3;
	public static final int ANKLE_ACCEL = 4;

	private static final Vector3f placeHolderVec = new Vector3f();
	private static final Quaternion placeHolderQuat = new Quaternion();

	// states for the legs
	private int leftLegState = STATE_UNKNOWN;
	private int rightLegState = STATE_UNKNOWN;

	// positions and rotations
	private Vector3f leftFootPosition = placeHolderVec;
	private Vector3f rightFootPosition = placeHolderVec;
	private Vector3f leftKneePosition = placeHolderVec;
	private Vector3f rightKneePosition = placeHolderVec;
	private Vector3f waistPosition = placeHolderVec;
	private Quaternion leftFootRotation = placeHolderQuat;
	private Quaternion rightFootRotation = placeHolderQuat;

	private Vector3f leftFootPositionCorrected = placeHolderVec;
	private Vector3f rightFootPositionCorrected = placeHolderVec;
	private Vector3f leftKneePositionCorrected = placeHolderVec;
	private Vector3f rightKneePositionCorrected = placeHolderVec;
	private Vector3f waistPositionCorrected = placeHolderVec;

	// velocities
	private Vector3f leftFootVelocity = placeHolderVec;
	private float leftFootVelocityMagnitude = 0;
	private Vector3f rightFootVelocity = placeHolderVec;
	private float rightFootVelocityMagnitude = 0;
	private float leftFootAngleDiff = 0;
	private float rightFootAngleDiff = 0;

	// acceleration
	private Vector3f leftFootAcceleration = placeHolderVec;
	private float leftFootAccelerationMagnitude = 0;
	private Vector3f rightFootAcceleration = placeHolderVec;
	private float rightFootAccelerationMagnitude = 0;

	// other data
	private long timeOfFrame = System.nanoTime();
	private LegTweakBuffer parent = null; // frame before this one
	private int frameNumber = 0; // higher number is older frame
	private int detectionMode = ANKLE_ACCEL; // detection mode
	private boolean accelerationAboveThresholdLeft = true;
	private boolean accelerationAboveThresholdRight = true;
	private float leftFloorLevel;
	private float rightFloorLevel;

	// hyperparameters
	public static final float SKATING_CUTOFF = 0.325f;
	private static final float SKATING_VELOCITY_CUTOFF = 5.25f;
	private static final float SKATING_ACCELERATION_CUTOFF = 1.50f;
	private static final float SKATING_ROTATIONAL_VELOCITY_CUTOFF = 2.8f;
	private static final float SKATING_LOCK_ENGAGE_PERCENT = 0.85f;
	private static final float FLOOR_DIF_CUTOFF = 0.1f;
	private static final float SIX_TRACKER_TOLLERANCE = 0.35f;

	private static final float PARAM_SCALAR_LOW_ACCEL = 2.5f;
	private static final float ACCEL_MAX_PARAM_SCALAR = 0.3f;
	private static final float ACCEL_MIN_PARAM_SCALAR = 1.25f;

	private float leftFootSensativity = 1.0f;
	private float rightFootSensativity = 1.0f;

	// NOTE TO SELF - one posibility to get less false negatives for locks is to
	// greatly increase all parameters for locking when acceleration is extremly
	// low (like 0.1)
	// this would be a good way to keep the foot locked with fast upper body
	// movement that causes high foot velocity

	private static final float SKATING_CUTOFF_ENGAGE = SKATING_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_VELOCITY_CUTOFF_ENGAGE = SKATING_VELOCITY_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ACCELERATION_CUTOFF_ENGAGE = SKATING_ACCELERATION_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE = SKATING_ROTATIONAL_VELOCITY_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;

	// getters and setters
	public Vector3f getLeftFootPosition() {
		return leftFootPosition.clone();
	}

	public void setLeftFootPosition(Vector3f leftFootPosition) {
		this.leftFootPosition = leftFootPosition.clone();
	}

	public Vector3f getRightFootPosition() {
		return rightFootPosition.clone();
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public Vector3f getLeftKneePosition() {
		return leftKneePosition.clone();
	}

	public void setLeftKneePosition(Vector3f leftKneePosition) {
		this.leftKneePosition = leftKneePosition.clone();
	}

	public Vector3f getRightKneePosition() {
		return rightKneePosition.clone();
	}

	public void setRightKneePosition(Vector3f rightKneePosition) {
		this.rightKneePosition = rightKneePosition.clone();
	}

	public Vector3f getWaistPosition() {
		return waistPosition.clone();
	}

	public void setWaistPosition(Vector3f waistPosition) {
		this.waistPosition = waistPosition.clone();
	}

	public Quaternion getLeftFootRotation() {
		return leftFootRotation.clone();
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation.clone();
	}

	public Quaternion getRightFootRotation() {
		return rightFootRotation.clone();
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation.clone();
	}

	public Vector3f getLeftFootPositionCorrected() {
		return leftFootPositionCorrected.clone();
	}

	public void setLeftFootPositionCorrected(Vector3f leftFootPositionCorrected) {
		this.leftFootPositionCorrected = leftFootPositionCorrected.clone();
	}

	public Vector3f getRightFootPositionCorrected() {
		return rightFootPositionCorrected.clone();
	}

	public void setRightFootPositionCorrected(Vector3f rightFootPositionCorrected) {
		this.rightFootPositionCorrected = rightFootPositionCorrected.clone();
	}

	public Vector3f getLeftKneePositionCorrected() {
		return leftKneePositionCorrected.clone();
	}

	public void setLeftKneePositionCorrected(Vector3f leftKneePositionCorrected) {
		this.leftKneePositionCorrected = leftKneePositionCorrected.clone();
	}

	public Vector3f getRightKneePositionCorrected() {
		return rightKneePositionCorrected.clone();
	}

	public void setRightKneePositionCorrected(Vector3f rightKneePositionCorrected) {
		this.rightKneePositionCorrected = rightKneePositionCorrected.clone();
	}

	public Vector3f getWaistPositionCorrected() {
		return waistPositionCorrected.clone();
	}

	public void setWaistPositionCorrected(Vector3f waistPositionCorrected) {
		this.waistPositionCorrected = waistPositionCorrected.clone();
	}

	public Vector3f getLeftFootVelocity() {
		return leftFootVelocity.clone();
	}

	public Vector3f getRightFootVelocity() {
		return rightFootVelocity.clone();
	}

	public void setLeftFloorLevel(float leftFloorLevel) {
		this.leftFloorLevel = leftFloorLevel;
	}

	public void setRightFloorLevel(float rightFloorLevel) {
		this.rightFloorLevel = rightFloorLevel;
	}

	public int getLeftLegState() {
		return leftLegState;
	}

	public void setLeftLegState(int leftLegState) {
		this.leftLegState = leftLegState;
	}

	public int getRightLegState() {
		return rightLegState;
	}

	public void setRightLegState(int rightLegState) {
		this.rightLegState = rightLegState;
	}

	public void setParent(LegTweakBuffer parent) {
		this.parent = parent;
	}

	public LegTweakBuffer getParent() {
		return parent;
	}

	public void setLeftFootAcceleration(Vector3f leftFootAcceleration) {
		this.leftFootAcceleration = leftFootAcceleration.clone();
	}

	public void setRightFootAcceleration(Vector3f rightFootAcceleration) {
		this.rightFootAcceleration = rightFootAcceleration.clone();
	}

	public void getLeftFootAcceleration(Vector3f leftFootAcceleration) {
		leftFootAcceleration.set(this.leftFootAcceleration);
	}

	public void getRightFootAcceleration(Vector3f rightFootAcceleration) {
		rightFootAcceleration.set(this.rightFootAcceleration);
	}

	public float getLeftFootAccelerationMagnitude() {
		return this.leftFootAcceleration.length();
	}

	public float getRightFootAccelerationMagnitude() {
		return this.rightFootAcceleration.length();
	}

	public float getLeftFootAccelerationY() {
		return this.leftFootAcceleration.y;
	}

	public float getRightFootAccelerationY() {
		return this.rightFootAcceleration.y;
	}

	public void setDetectionMode(int mode) {
		this.detectionMode = mode;
	}

	// calculate momvent attributes
	public void calculateFootAttributes(boolean active) {
		updateFrameNumber(0);

		// compute attributes of the legs
		computeVelocity();
		computeAccelerationMagnitude();

		// check if the acceleration triggers forced unlock
		if (detectionMode == FOOT_ACCEL) {
			computeAccelerationAboveThresholdFootTrackers();
		} else {
			computeAccelerationAboveThresholdAnkleTrackers();
		}

		// calculate the scalar for other parameters
		leftFootSensativity = getLeftFootScalar();
		rightFootSensativity = getRightFootScalar();

		// if correction is inactive state is unknown (default to unlocked)
		if (!active) {
			leftLegState = UNLOCKED;
			rightLegState = UNLOCKED;
		} else {
			computeState();
		}
	}

	// update the frame number of all the frames
	public void updateFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
		if (this.frameNumber >= 10) {
			this.parent = null; // once a frame is 10 frames old, it is no
								// longer
								// needed
		}
		if (parent != null) {
			parent.updateFrameNumber(frameNumber + 1);
		}
	}

	// compute the state of the legs
	private void computeState() {
		// based on the last state of the legs compute their state for this
		// individual frame
		leftLegState = checkStateLeft();

		rightLegState = checkStateRight();
	}

	// check if a locked foot should stay locked or be released
	private int checkStateLeft() {
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / 1000000000.0f);
		if (parent.leftLegState == UNLOCKED) {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| leftFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE
					|| leftFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DIF_CUTOFF
					|| accelerationAboveThresholdLeft
			) {
				return UNLOCKED;
			}
			return LOCKED;

		} else {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF
					|| leftFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_CUTOFF * leftFootSensativity
					|| leftFootAngleDiff * timeStep
						> SKATING_ROTATIONAL_VELOCITY_CUTOFF * leftFootSensativity
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DIF_CUTOFF
					|| accelerationAboveThresholdLeft
			) {
				return UNLOCKED;
			}
			return LOCKED;
		}
	}

	// check if a locked foot should stay locked or be released
	private int checkStateRight() {
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / 1000000000.0f);
		if (parent.rightLegState == UNLOCKED) {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| rightFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_CUTOFF_ENGAGE * rightFootSensativity
					|| rightFootAngleDiff * timeStep
						> SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
							* rightFootSensativity
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DIF_CUTOFF
					|| accelerationAboveThresholdRight
			) {
				return UNLOCKED;
			}
			return LOCKED;
		} else {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF
					|| rightFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF
					|| rightFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DIF_CUTOFF
					|| accelerationAboveThresholdRight
			) {
				return UNLOCKED;
			}
			return LOCKED;
		}
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet disregarding vertical displacment
	private float getLeftFootHorizantalDifference() {
		return leftFootPositionCorrected.subtract(leftFootPosition).setY(0).length();
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet
	private float getRightFootHorizantalDifference() {
		return rightFootPositionCorrected.subtract(rightFootPosition).setY(0).length();
	}

	// get the angular velocity of the left foot (kinda we just want a scalar)
	private float getLeftFootAngularVelocity() {
		return leftFootRotation
			.getRotationColumn(2)
			.distance(parent.leftFootRotation.getRotationColumn(2));
	}

	// get the angular velocity of the right foot (kinda we just want a scalar)
	private float getRightFootAngularVelocity() {
		return rightFootRotation
			.getRotationColumn(2)
			.distance(parent.rightFootRotation.getRotationColumn(2));
	}

	// compute the velocity of the feet from the position in the last frames
	private void computeVelocity() {
		if (parent != null) {
			leftFootVelocity = leftFootPosition.subtract(parent.leftFootPosition);
			leftFootVelocityMagnitude = leftFootVelocity.length();
			rightFootVelocity = rightFootPosition.subtract(parent.rightFootPosition);
			rightFootVelocityMagnitude = rightFootVelocity.length();
			leftFootAngleDiff = getLeftFootAngularVelocity();
			rightFootAngleDiff = getRightFootAngularVelocity();
		}
	}

	// get the nth parent of this frame
	private LegTweakBuffer getNParent(int n) {
		if (n == 0 || parent == null) {
			return this;
		} else {
			return parent.getNParent(n - 1);
		}
	}

	// compute the acceleration magnitude of the feet from the acceleration
	// given by the imus (exclude y)
	private void computeAccelerationMagnitude() {
		leftFootAccelerationMagnitude = leftFootAcceleration.setY(0).length();
		rightFootAccelerationMagnitude = rightFootAcceleration.setY(0).length();
	}

	// for 8 trackers the data from the imus is enough to determine lock/unlock
	private void computeAccelerationAboveThresholdFootTrackers() {
		accelerationAboveThresholdLeft = leftFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF_ENGAGE;
		accelerationAboveThresholdRight = rightFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF_ENGAGE;
	}

	// for any setup without foot trackers the data from the imus is enough to
	// determine lock/unlock
	private void computeAccelerationAboveThresholdAnkleTrackers() {
		accelerationAboveThresholdLeft = leftFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF + SIX_TRACKER_TOLLERANCE;
		accelerationAboveThresholdRight = rightFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF + SIX_TRACKER_TOLLERANCE;
	}

	// calculate the scalar to apply to the non acceleration based lock/unlock
	// hyperparameters
	private float getLeftFootScalar() {
		if (parent.leftLegState == LOCKED) {
			if (leftFootAccelerationMagnitude < ACCEL_MAX_PARAM_SCALAR) {
				return PARAM_SCALAR_LOW_ACCEL;
			} else if (leftFootAccelerationMagnitude > ACCEL_MIN_PARAM_SCALAR) {
				return PARAM_SCALAR_LOW_ACCEL
					* (leftFootAccelerationMagnitude - ACCEL_MIN_PARAM_SCALAR)
					/ (ACCEL_MAX_PARAM_SCALAR - ACCEL_MIN_PARAM_SCALAR);
			}
		}
		return 1.0f;
	}

	private float getRightFootScalar() {
		if (parent.rightLegState == LOCKED) {
			if (rightFootAccelerationMagnitude < ACCEL_MAX_PARAM_SCALAR) {
				return PARAM_SCALAR_LOW_ACCEL;
			} else if (rightFootAccelerationMagnitude > ACCEL_MIN_PARAM_SCALAR) {
				return PARAM_SCALAR_LOW_ACCEL
					* (rightFootAccelerationMagnitude - ACCEL_MIN_PARAM_SCALAR)
					/ (ACCEL_MAX_PARAM_SCALAR - ACCEL_MIN_PARAM_SCALAR);
			}
		}
		return 1.0f;
	}

}
