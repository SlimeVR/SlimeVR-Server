package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


/**
 * class that holds data related to the state and other variuse attributes of
 * the legs such as the position of the foot, knee, and waist, after and before
 * correction, the velocity of the foot and the computed state of the feet at
 * that frame. mainly calculates the state of the legs per frame using these
 * rules: The conditions for an unlock are as follows: 1. the foot is to far
 * from its correct position 2. a velocity higher than a threashold is achived
 * 3. a large acceleration is applied to the foot 4. angular velocity of the
 * foot goes higher than a threashold. The conditions for a lock are the
 * opposite of the above but require a lower value for all of the above
 * conditions
 */

public class LegTweakBuffer {
	public static final int STATE_UNKNOWN = 0; // fall back state
	public static final int LOCKED = 1;
	public static final int UNLOCKED = 2;
	public static final int FOOT_ACCEL = 3;
	public static final int ANKLE_ACCEL = 4;

	public static final float NS_CONVERT = 1000000000.0f;
	private static final Vector3f placeHolderVec = new Vector3f();
	private static final Quaternion placeHolderQuat = new Quaternion();
	private static final Vector3f gravity = new Vector3f(0, -9.81f, 0);

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
	private Vector3f centerOfMass = placeHolderVec;
	private Vector3f centerOfMassVelocity = placeHolderVec;
	private Vector3f centerOfMassAcceleration = placeHolderVec;
	private float leftFloorLevel;
	private float rightFloorLevel;

	// hyperparameters
	public static final float SKATING_DISTANCE_CUTOFF = 0.5f;
	private static final float SKATING_VELOCITY_THRESHOLD = 3.25f;
	private static final float SKATING_ACCELERATION_THRESHOLD = 1.00f;
	private static final float SKATING_ROTVELOCITY_THRESHOLD = 4.5f;
	private static final float SKATING_LOCK_ENGAGE_PERCENT = 0.85f;
	private static final float SKATING_ACCELERATION_Y_USE_PERCENT = 0.25f;
	private static final float FLOOR_DISTANCE_CUTOFF = 0.125f;
	private static final float SIX_TRACKER_TOLLERANCE = -0.10f;
	private static final Vector3f FORCE_VECTOR_TO_PRESSURE = new Vector3f(0.25f, 1.0f, 0.25f);
	private static final float FORCE_ERROR_TOLLERANCE = 4.0f;
	private static final float[] FORCE_VECTOR_FALLBACK = new float[] { 0.1f, 0.1f };

	private static final float PARAM_SCALAR_MAX = 3.2f;
	private static final float PARAM_SCALAR_MIN = 0.25f;
	private static final float PARAM_SCALAR_MID = 1.0f;

	// the point at which the scalar is at the max or min depending on accel
	private static final float MAX_SCALAR_ACCEL = 0.2f;
	private static final float MIN_SCALAR_ACCEL = 0.9f;

	// the point at which the scalar is at it max or min in a double locked foot
	// situation
	private static final float MAX_SCALAR_DORMANT = 0.2f;
	private static final float MIN_SCALAR_DORMANT = 1.50f;

	// the point at which the scalar is at it max or min in a single locked foot
	// situation
	private static final float MIN_SCALAR_ACTIVE = 1.75f;
	private static final float MAX_SCALAR_ACTIVE = 0.1f;

	// maximum scalers for the pressure on each foot
	private static final float PRESSURE_SCALER_MIN = 0.1f;
	private static final float PRESSURE_SCALER_MAX = 1.9f;

	private float leftFootSensitivityVel = 1.0f;
	private float rightFootSensitivityVel = 1.0f;
	private float leftFootSensitivityAccel = 1.0f;
	private float rightFootSensitivityAccel = 1.0f;

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
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftFootPosition);
	}

	public void setLeftFootPosition(Vector3f leftFootPosition) {
		this.leftFootPosition = leftFootPosition.clone();
	}

	public Vector3f getRightFootPosition(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(rightFootPosition);
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public Vector3f getLeftKneePosition(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftKneePosition);
	}

	public void setLeftKneePosition(Vector3f leftKneePosition) {
		this.leftKneePosition = leftKneePosition.clone();
	}

	public Vector3f getRightKneePosition(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();
		return vec.set(rightKneePosition);
	}

	public void setRightKneePosition(Vector3f rightKneePosition) {
		this.rightKneePosition = rightKneePosition.clone();
	}

	public Vector3f getWaistPosition(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(waistPosition);
	}

	public void setWaistPosition(Vector3f waistPosition) {
		this.waistPosition = waistPosition.clone();
	}

	public Quaternion getLeftFootRotation(Quaternion quat) {
		if (quat == null)
			quat = new Quaternion();

		return quat.set(leftFootRotation);
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation.clone();
	}

	public Quaternion getRightFootRotation(Quaternion quat) {
		if (quat == null)
			quat = new Quaternion();

		return quat.set(rightFootRotation);
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation.clone();
	}

	public Vector3f getLeftFootPositionCorrected(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftFootPositionCorrected);
	}

	public void setLeftFootPositionCorrected(Vector3f leftFootPositionCorrected) {
		this.leftFootPositionCorrected = leftFootPositionCorrected.clone();
	}

	public Vector3f getRightFootPositionCorrected(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(rightFootPositionCorrected);
	}

	public void setRightFootPositionCorrected(Vector3f rightFootPositionCorrected) {
		this.rightFootPositionCorrected = rightFootPositionCorrected.clone();
	}

	public Vector3f getLeftKneePositionCorrected(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftKneePositionCorrected);
	}

	public void setLeftKneePositionCorrected(Vector3f leftKneePositionCorrected) {
		this.leftKneePositionCorrected = leftKneePositionCorrected.clone();
	}

	public Vector3f getRightKneePositionCorrected(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(rightKneePositionCorrected);
	}

	public void setRightKneePositionCorrected(Vector3f rightKneePositionCorrected) {
		this.rightKneePositionCorrected = rightKneePositionCorrected.clone();
	}

	public Vector3f getWaistPositionCorrected(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(waistPositionCorrected);
	}

	public void setWaistPositionCorrected(Vector3f waistPositionCorrected) {
		this.waistPositionCorrected = waistPositionCorrected.clone();
	}

	public Vector3f getLeftFootVelocity(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftFootVelocity);
	}

	public Vector3f getRightFootVelocity(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(rightFootVelocity);
	}

	public Vector3f getCenterOfMass(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

		return vec.set(centerOfMass);
	}

	public void setCenterOfMass(Vector3f centerOfMass) {
		this.centerOfMass = centerOfMass.clone();
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
		if (vec == null)
			vec = new Vector3f();

		return vec.set(leftFootAcceleration);
	}

	public Vector3f getRightFootAcceleration(Vector3f vec) {
		if (vec == null)
			vec = new Vector3f();

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

	public float getLeftFloorLevel() {
		return leftFloorLevel;
	}

	public float getRightFloorLevel() {
		return rightFloorLevel;
	}

	// returns 1 / delta time
	public float getTimeDelta() {
		if (parent == null)
			return 0.0f;

		return 1.0f / ((timeOfFrame - parent.timeOfFrame) / NS_CONVERT);
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
		computeComAtributes();

		// check if the acceleration triggers forced unlock
		if (detectionMode == FOOT_ACCEL) {
			computeAccelerationAboveThresholdFootTrackers();
		} else {
			computeAccelerationAboveThresholdAnkleTrackers();
		}

		// calculate the scalar for other parameters
		computeScalar();

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

		if (this.frameNumber >= 10)
			this.parent = null;

		if (parent != null)
			parent.updateFrameNumber(frameNumber + 1);
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
		float timeStep = getTimeDelta();
		if (parent.leftLegState == UNLOCKED) {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| leftFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_CUTOFF_ENGAGE * leftFootSensitivityVel
					|| leftFootAngleDiff * timeStep
						> SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE * leftFootSensitivityVel
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DISTANCE_CUTOFF
					|| accelerationAboveThresholdLeft
			) {
				return UNLOCKED;
			}

			return LOCKED;
		}

		if (
			parent.getLeftFootHorizantalDifference() > SKATING_DISTANCE_CUTOFF
				|| leftFootVelocityMagnitude * timeStep
					> SKATING_VELOCITY_THRESHOLD * leftFootSensitivityVel
				|| leftFootAngleDiff * timeStep
					> SKATING_ROTVELOCITY_THRESHOLD * leftFootSensitivityVel
				|| leftFootPosition.y > leftFloorLevel + FLOOR_DISTANCE_CUTOFF
				|| accelerationAboveThresholdLeft
		) {
			return UNLOCKED;
		}

		return LOCKED;
	}

	// check if a locked foot should stay locked or be released
	private int checkStateRight() {
		float timeStep = getTimeDelta();

		if (parent.rightLegState == UNLOCKED) {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| rightFootVelocityMagnitude * timeStep
						> SKATING_VELOCITY_CUTOFF_ENGAGE * leftFootSensitivityVel
					|| rightFootAngleDiff * timeStep
						> SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE * leftFootSensitivityVel
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
					|| accelerationAboveThresholdRight
			) {
				return UNLOCKED;
			}

			return LOCKED;
		}

		if (
			parent.getRightFootHorizantalDifference() > SKATING_DISTANCE_CUTOFF
				|| rightFootVelocityMagnitude * timeStep
					> SKATING_VELOCITY_THRESHOLD * rightFootSensitivityVel
				|| rightFootAngleDiff * timeStep
					> SKATING_ROTVELOCITY_THRESHOLD * rightFootSensitivityVel
				|| rightFootPosition.y > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
				|| accelerationAboveThresholdRight
		) {
			return UNLOCKED;
		}

		return LOCKED;
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
		if (parent == null)
			return;

		leftFootVelocity = leftFootPosition.subtract(parent.leftFootPosition);
		leftFootVelocityMagnitude = leftFootVelocity.length();
		rightFootVelocity = rightFootPosition.subtract(parent.rightFootPosition);
		rightFootVelocityMagnitude = rightFootVelocity.length();
		leftFootAngleDiff = getLeftFootAngularVelocity();
		rightFootAngleDiff = getRightFootAngularVelocity();
	}

	// get the nth parent of this frame
	private LegTweakBuffer getNParent(int n) {
		if (n == 0 || parent == null)
			return this;

		return parent.getNParent(n - 1);
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

	// compute the velocity and acceleration of the center of mass
	private void computeComAtributes() {
		centerOfMassVelocity = centerOfMass.subtract(parent.centerOfMass);
		centerOfMassAcceleration = centerOfMassVelocity.subtract(parent.centerOfMassVelocity);
	}

	// for 8 trackers the data from the imus is enough to determine lock/unlock
	private void computeAccelerationAboveThresholdFootTrackers() {
		accelerationAboveThresholdLeft = leftFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF_ENGAGE * leftFootSensitivityAccel;
		accelerationAboveThresholdRight = rightFootAccelerationMagnitude
			> SKATING_ACCELERATION_CUTOFF_ENGAGE * rightFootSensitivityAccel;
	}

	// for any setup without foot trackers the data from the imus is enough to
	// determine lock/unlock
	private void computeAccelerationAboveThresholdAnkleTrackers() {
		accelerationAboveThresholdLeft = leftFootAccelerationMagnitude
			> (SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLLERANCE) * leftFootSensitivityAccel;
		accelerationAboveThresholdRight = rightFootAccelerationMagnitude
			> (SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLLERANCE) * rightFootSensitivityAccel;
	}

	// using the parent lock/unlock states, velocity, and acceleration,
	// determine the scalars to apply to the hyperparameters when computing the
	// lock state
	private void computeScalar() {
		// get the first set of scalars that are based on acceleration from the
		// imus
		float leftFootScalarAccel = getLeftFootScalarAccel();
		float rightFootScalarAccel = getRightFootScalarAccel();

		// get the second set of scalars that is based of of how close each foot
		// is to a lock and dynamically adjusting the scalars
		// (based off the assumption that if you are standing one foot is likly
		// planted on the ground unless you are moving fast)
		float leftFootScalarVel = getLeftFootLockLiklyHood();
		float rightFootScalarVel = getRightFootLockLiklyHood();

		// get the third set of scalars that is based on where the COM is
		float[] pressureScalars = getPressurePrediction();

		// combine the scalars to get the final scalars
		leftFootSensitivityVel = (leftFootScalarAccel
			+ leftFootScalarVel / 2.0f)
			* clamp(PRESSURE_SCALER_MIN, PRESSURE_SCALER_MAX, pressureScalars[0] * 2.0f);
		rightFootSensitivityVel = (rightFootScalarAccel
			+ rightFootScalarVel / 2.0f)
			* clamp(PRESSURE_SCALER_MIN, PRESSURE_SCALER_MAX, pressureScalars[1] * 2.0f);

		leftFootSensitivityAccel = leftFootScalarVel;
		rightFootSensitivityAccel = rightFootScalarVel;
	}

	// calculate a scalar using acceleration to apply to the non acceleration
	// based hyperparameters when calculating
	// lock states
	private float getLeftFootScalarAccel() {
		if (leftLegState == LOCKED) {
			if (leftFootAccelerationMagnitude < MAX_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX;
			} else if (leftFootAccelerationMagnitude > MIN_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX
					* (leftFootAccelerationMagnitude - MIN_SCALAR_ACCEL)
					/ (MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL);
			}
		}

		return PARAM_SCALAR_MID;
	}

	private float getRightFootScalarAccel() {
		if (rightLegState == LOCKED) {
			if (rightFootAccelerationMagnitude < MAX_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX;
			} else if (rightFootAccelerationMagnitude > MIN_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX
					* (rightFootAccelerationMagnitude - MIN_SCALAR_ACCEL)
					/ (MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL);
			}
		}

		return PARAM_SCALAR_MID;
	}

	// calculate a scalar using the velocity of the foot trackers and the lock
	// states to calculate a scalar to apply to the non acceleration based
	// hyperparameters when calculating
	// lock states
	private float getLeftFootLockLiklyHood() {
		if (leftLegState == LOCKED && rightLegState == LOCKED) {
			Vector3f velocityDiff = leftFootVelocity.subtract(rightFootVelocity);
			velocityDiff.setY(0.0f);
			float velocityDiffMagnitude = velocityDiff.length();

			if (velocityDiffMagnitude < MAX_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX;
			} else if (velocityDiffMagnitude > MIN_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX
					* (velocityDiffMagnitude - MIN_SCALAR_DORMANT)
					/ (MAX_SCALAR_DORMANT - MIN_SCALAR_DORMANT);
			}
		}

		// calculate the 'unlockedness factor' and use that to
		// determine the scalar (go as low as 0.5 as as high as
		// param_scalar_max)
		float velocityDifAbs = Math.abs(leftFootVelocityMagnitude)
			- Math.abs(rightFootVelocityMagnitude);

		if (velocityDifAbs > MIN_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MIN;
		} else if (velocityDifAbs < MAX_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MAX;
		}

		return PARAM_SCALAR_MAX
			* (velocityDifAbs - MIN_SCALAR_ACTIVE)
			/ (MAX_SCALAR_ACTIVE - MIN_SCALAR_ACTIVE)
			- PARAM_SCALAR_MID;
	}

	private float getRightFootLockLiklyHood() {
		if (rightLegState == LOCKED && leftLegState == LOCKED) {
			Vector3f velocityDiff = rightFootVelocity.subtract(leftFootVelocity);
			velocityDiff.setY(0.0f);
			float velocityDiffMagnitude = velocityDiff.length();

			if (velocityDiffMagnitude < MAX_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX;
			} else if (velocityDiffMagnitude > MIN_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX
					* (velocityDiffMagnitude - MIN_SCALAR_DORMANT)
					/ (MAX_SCALAR_DORMANT - MIN_SCALAR_DORMANT);
			}
		}

		// calculate the 'unlockedness factor' and use that to
		// determine the scalar (go as low as 0.5 as as high as
		// param_scalar_max)
		float velocityDifAbs = Math.abs(rightFootVelocityMagnitude)
			- Math.abs(leftFootVelocityMagnitude);

		if (velocityDifAbs > MIN_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MIN;
		} else if (velocityDifAbs < MAX_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MAX;
		}

		return PARAM_SCALAR_MAX
			* (velocityDifAbs - MIN_SCALAR_ACTIVE)
			/ (MAX_SCALAR_ACTIVE - MIN_SCALAR_ACTIVE)
			- PARAM_SCALAR_MID;
	}

	// get the pressure prediction for the feet based of the center of mass
	// (assume mass is 1)
	// for understanding in the future this is assuming that the mass is one and
	// the force of gravity
	// is 9.8 m/s^2 this allows for the force sum to map directly to the
	// acceleration of the center of mass
	// since F = ma and if m is 1 then F = a
	private float[] getPressurePrediction() {
		float leftFootPressure = 0;
		float rightFootPressure = 0;

		// get the vector's from the com to each foot
		Vector3f leftFootVector = leftFootPosition.subtract(centerOfMass).normalize();
		Vector3f rightFootVector = rightFootPosition.subtract(centerOfMass).normalize();

		// get the magnitude of the force on each foot
		float leftFootMagnitude = 9.81f * leftFootVector.y / leftFootVector.length();
		float rightFootMagnitude = 9.81f * rightFootVector.y / rightFootVector.length();

		// get the force vector each foot could apply to the com
		Vector3f leftFootForce = leftFootVector.mult(leftFootMagnitude / 2.0f);
		Vector3f rightFootForce = rightFootVector.mult(rightFootMagnitude / 2.0f);

		// based of the acceleration of the com, get the force each foot is
		// likly applying (the expected force sum should be equal to
		// centerOfMassAcceleration since the mass is 1)
		findForceVectors(leftFootForce, rightFootForce);

		// see if the force vectors found a reasonable solution
		// if they did not we assume there is another force acting on the com
		// and fall back to a low pressure prediction
		if (detectOutsideForces(leftFootForce, rightFootForce))
			return FORCE_VECTOR_FALLBACK;

		// set the pressure to the force on each foot times the force to
		// pressure scalar
		leftFootPressure = leftFootForce.mult(FORCE_VECTOR_TO_PRESSURE).length();
		rightFootPressure = rightFootForce.mult(FORCE_VECTOR_TO_PRESSURE).length();

		// distance from the ground is a factor in the pressure
		// using the inverse of the distance to the ground scale the
		// pressure
		float leftDistance = (leftFootPosition.y > leftFloorLevel)
			? (leftFootPosition.y - leftFloorLevel)
			: LegTweaks.NEARLY_ZERO;
		leftFootPressure *= 1.0f / (leftDistance);
		float rightDistance = (rightFootPosition.y > rightFloorLevel)
			? (rightFootPosition.y - rightFloorLevel)
			: LegTweaks.NEARLY_ZERO;
		rightFootPressure *= 1.0f / (rightDistance);

		// normalize the pressure values
		float pressureSum = leftFootPressure + rightFootPressure;
		leftFootPressure /= pressureSum;
		rightFootPressure /= pressureSum;

		return new float[] { leftFootPressure, rightFootPressure };
	}

	// preform a gradient descent to find the force vectors that best match the
	// acceleration of the com
	private void findForceVectors(Vector3f leftFootForce, Vector3f rightFootForce) {
		int iterations = 100;
		float stepSize = 0.01f;
		// setup the temporary variables
		Vector3f tempLeftFootForce1 = leftFootForce.clone();
		Vector3f tempLeftFootForce2 = leftFootForce.clone();
		Vector3f tempRightFootForce1 = rightFootForce.clone();
		Vector3f tempRightFootForce2 = rightFootForce.clone();
		Vector3f error;
		Vector3f error1;
		Vector3f error2;
		Vector3f error3;
		Vector3f error4;

		for (int i = 0; i < iterations; i++) {
			tempLeftFootForce1.set(leftFootForce);
			tempLeftFootForce2.set(leftFootForce);
			tempRightFootForce1.set(rightFootForce);
			tempRightFootForce2.set(rightFootForce);

			// get the error at the current position
			error = centerOfMassAcceleration
				.subtract(leftFootForce.add(rightFootForce).add(gravity));

			// add and subtract the error to the force vectors
			tempLeftFootForce1 = tempLeftFootForce1.mult(1.0f + stepSize);
			tempLeftFootForce2 = tempLeftFootForce2.mult(1.0f - stepSize);
			tempRightFootForce1 = tempRightFootForce1.mult(1.0f + stepSize);
			tempRightFootForce2 = tempRightFootForce2.mult(1.0f - stepSize);

			// get the error at the new position
			error1 = getForceVectorError(tempLeftFootForce1, rightFootForce);
			error2 = getForceVectorError(tempLeftFootForce2, rightFootForce);
			error3 = getForceVectorError(tempRightFootForce1, leftFootForce);
			error4 = getForceVectorError(tempRightFootForce2, leftFootForce);

			// set the new force vectors
			if (error1.length() < error.length()) {
				leftFootForce.set(tempLeftFootForce1);
			} else if (error2.length() < error.length()) {
				leftFootForce.set(tempLeftFootForce2);
			}

			if (error3.length() < error.length()) {
				rightFootForce.set(tempRightFootForce1);
			} else if (error4.length() < error.length()) {
				rightFootForce.set(tempRightFootForce2);
			}
		}
	}

	// detect any outside forces on the body such
	// as a wall or a chair. returns true if there is a outside force
	private boolean detectOutsideForces(Vector3f f1, Vector3f f2) {
		Vector3f force = gravity.add(f1).add(f2);
		Vector3f error = centerOfMassAcceleration.subtract(force);
		return error.length() > FORCE_ERROR_TOLLERANCE;
	}

	// simple error function for the force vector gradient descent
	private Vector3f getForceVectorError(Vector3f testForce, Vector3f otherForce) {
		return centerOfMassAcceleration
			.subtract(testForce.add(otherForce).add(gravity));
	}

	// clamp a float between two values
	private float clamp(float min, float max, float val) {
		return Math.min(max, Math.max(min, val));
	}
}
