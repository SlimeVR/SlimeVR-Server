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

	public static final float NS_CONVERT = 1000000000.0f;
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
	public static final float SKATING_DISTANCE_CUTOFF = 0.225f;
	private static final float SKATING_VELOCITY_THRESHOLD = 4.25f;
	private static final float SKATING_ACCELERATION_THRESHOLD = 1.15f;
	private static final float SKATING_ROTVELOCITY_THRESHOLD = 4.5f;
	private static final float SKATING_LOCK_ENGAGE_PERCENT = 0.9f;
	private static final float SKATING_ACCELERATION_Y_USE_PERCENT = 0.25f;
	private static final float FLOOR_DISTANCE_CUTOFF = 0.1f;
	private static final float SIX_TRACKER_TOLLERANCE = 0.10f;

	private static final float PARAM_SCALAR_MAX = 2.0f;
	private static final float MAX_SCALAR_ACCEL = 0.3f;
	private static final float MIN_SCALAR_ACCEL = 1.0f;

	private float leftFootSensitivity = 1.0f;
	private float rightFootSensitivity = 1.0f;


	private static final float SKATING_CUTOFF_ENGAGE = SKATING_DISTANCE_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_VELOCITY_CUTOFF_ENGAGE = SKATING_VELOCITY_THRESHOLD
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ACCELERATION_CUTOFF_ENGAGE = SKATING_ACCELERATION_THRESHOLD
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE = SKATING_ROTVELOCITY_THRESHOLD
		* SKATING_LOCK_ENGAGE_PERCENT;

	// getters and setters
	public Vector3f getLeftFootPosition(Vector3f vec) {
		return vec.set(leftFootPosition);
	}

	public void setLeftFootPosition(Vector3f leftFootPosition) {
		this.leftFootPosition = leftFootPosition.clone();
	}

	public Vector3f getRightFootPosition(Vector3f vec) {
		return vec.set(rightFootPosition);
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public Vector3f getLeftKneePosition(Vector3f vec) {
		return vec.set(leftKneePosition);
	}

	public void setLeftKneePosition(Vector3f leftKneePosition) {
		this.leftKneePosition = leftKneePosition.clone();
	}

	public Vector3f getRightKneePosition(Vector3f vec) {
		return vec.set(rightKneePosition);
	}

	public void setRightKneePosition(Vector3f rightKneePosition) {
		this.rightKneePosition = rightKneePosition.clone();
	}

	public Vector3f getWaistPosition(Vector3f vec) {
		return vec.set(waistPosition);
	}

	public void setWaistPosition(Vector3f waistPosition) {
		this.waistPosition = waistPosition.clone();
	}

	public Quaternion getLeftFootRotation(Quaternion quat) {
		return quat.set(leftFootRotation);
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation.clone();
	}

	public Quaternion getRightFootRotation(Quaternion quat) {
		return quat.set(rightFootRotation);
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation.clone();
	}

	public Vector3f getLeftFootPositionCorrected(Vector3f vec) {
		return vec.set(leftFootPositionCorrected);
	}

	public void setLeftFootPositionCorrected(Vector3f leftFootPositionCorrected) {
		this.leftFootPositionCorrected = leftFootPositionCorrected.clone();
	}

	public Vector3f getRightFootPositionCorrected(Vector3f vec) {
		return vec.set(rightFootPositionCorrected);
	}

	public void setRightFootPositionCorrected(Vector3f rightFootPositionCorrected) {
		this.rightFootPositionCorrected = rightFootPositionCorrected.clone();
	}

	public Vector3f getLeftKneePositionCorrected(Vector3f vec) {
		return vec.set(leftKneePositionCorrected);
	}

	public void setLeftKneePositionCorrected(Vector3f leftKneePositionCorrected) {
		this.leftKneePositionCorrected = leftKneePositionCorrected.clone();
	}

	public Vector3f getRightKneePositionCorrected(Vector3f vec) {
		return vec.set(rightKneePositionCorrected);
	}

	public void setRightKneePositionCorrected(Vector3f rightKneePositionCorrected) {
		this.rightKneePositionCorrected = rightKneePositionCorrected.clone();
	}

	public Vector3f getWaistPositionCorrected(Vector3f vec) {
		return vec.set(waistPositionCorrected);
	}

	public void setWaistPositionCorrected(Vector3f waistPositionCorrected) {
		this.waistPositionCorrected = waistPositionCorrected.clone();
	}

	public Vector3f getLeftFootVelocity(Vector3f vec) {
		return vec.set(leftFootVelocity);
	}

	public Vector3f getRightFootVelocity(Vector3f vec) {
		return vec.set(rightFootVelocity);
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

	public Vector3f getLeftFootAcceleration(Vector3f vec) {
		return vec.set(leftFootAcceleration);
	}

	public Vector3f getRightFootAcceleration(Vector3f vec) {
		return vec.set(rightFootAcceleration);
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
		leftFootSensitivity = getLeftFootScalar();
		rightFootSensitivity = getRightFootScalar();

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
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / NS_CONVERT);
		if (parent.leftLegState == UNLOCKED) {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| leftFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE
					|| leftFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DISTANCE_CUTOFF
					|| accelerationAboveThresholdLeft
			) {
				return UNLOCKED;
			}
			return LOCKED;

		} else {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_DISTANCE_CUTOFF
					|| leftFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_THRESHOLD * leftFootSensitivity
					|| leftFootAngleDiff * timeStep
						> SKATING_ROTVELOCITY_THRESHOLD * leftFootSensitivity
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DISTANCE_CUTOFF
					|| accelerationAboveThresholdLeft
			) {
				return UNLOCKED;
			}
			return LOCKED;
		}
	}

	// check if a locked foot should stay locked or be released
	private int checkStateRight() {
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / NS_CONVERT);
		if (parent.rightLegState == UNLOCKED) {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| rightFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE
					|| rightFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
					|| accelerationAboveThresholdRight
			) {
				return UNLOCKED;
			}
			return LOCKED;
		} else {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_DISTANCE_CUTOFF
					|| rightFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_THRESHOLD * rightFootSensitivity
					|| rightFootAngleDiff * timeStep
						> SKATING_ROTVELOCITY_THRESHOLD * rightFootSensitivity
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
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
		leftFootAccelerationMagnitude = leftFootAcceleration
			.setY(leftFootAcceleration.y * SKATING_ACCELERATION_Y_USE_PERCENT)
			.length();
		rightFootAccelerationMagnitude = rightFootAcceleration
			.setY(rightFootAcceleration.y * SKATING_ACCELERATION_Y_USE_PERCENT)
			.length();
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
			> SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLLERANCE;
		accelerationAboveThresholdRight = rightFootAccelerationMagnitude
			> SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLLERANCE;
	}

	// calculate the scalar to apply to the non acceleration based lock/unlock
	// hyperparameters
	private float getLeftFootScalar() {
		if (parent.leftLegState == LOCKED) {
			if (leftFootAccelerationMagnitude < MAX_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX;
			} else if (leftFootAccelerationMagnitude > MIN_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX
					* (leftFootAccelerationMagnitude - MIN_SCALAR_ACCEL)
					/ (MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL);
			}
		}
		return 1.0f;
	}

	private float getRightFootScalar() {
		if (parent.rightLegState == LOCKED) {
			if (rightFootAccelerationMagnitude < MAX_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX;
			} else if (rightFootAccelerationMagnitude > MIN_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX
					* (rightFootAccelerationMagnitude - MIN_SCALAR_ACCEL)
					/ (MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL);
			}
		}
		return 1.0f;
	}

}
