package dev.slimevr.tracking.processor.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import io.github.axisangles.ktmath.Vector3;


/**
 * class that holds data related to the state and other variuse attributes of
 * the legs such as the position of the foot, knee, and waist, after and before
 * correction, the velocity of the foot and the computed state of the feet at
 * that frame. mainly calculates the state of the legs per frame using these
 * rules: The conditions for an unlock are as follows: 1. the foot is to far
 * from its correct position 2. a velocity higher than a threashold is achived
 * 3. a large acceleration is applied to the foot 4. angular velocity of the
 * foot goes higher than a threshold. The conditions for a lock are the opposite
 * of the above but require a lower value for all of the above conditions. The
 * afformentioned thresholds are computed by applying scalers to a base
 * threshold value. This allows one set of initial values to be applicable to a
 * large range of actions and body types.
 */

public class LegTweakBuffer {
	public static final int STATE_UNKNOWN = 0; // fall back state
	public static final int LOCKED = 1;
	public static final int UNLOCKED = 2;
	public static final int FOOT_ACCEL = 3;
	public static final int ANKLE_ACCEL = 4;

	public static final float NS_CONVERT = 1.0e9f;
	private static final Vector3 GRAVITY = new Vector3(0, -9.81f, 0);
	private static final float GRAVITY_MAGNITUDE = GRAVITY.length();
	private static final int BUFFER_LEN = 10;

	// states for the legs
	private int leftLegState = STATE_UNKNOWN;
	private int rightLegState = STATE_UNKNOWN;

	// positions and rotations
	private Vector3 leftFootPosition = new Vector3();
	private Vector3 rightFootPosition = new Vector3();
	private Vector3 leftKneePosition = new Vector3();
	private Vector3 rightKneePosition = new Vector3();
	private Vector3 waistPosition = new Vector3();
	private Quaternion leftFootRotation = new Quaternion();
	private Quaternion rightFootRotation = new Quaternion();

	private Vector3 leftFootPositionCorrected = new Vector3();
	private Vector3 rightFootPositionCorrected = new Vector3();
	private Vector3 leftKneePositionCorrected = new Vector3();
	private Vector3 rightKneePositionCorrected = new Vector3();
	private Vector3 waistPositionCorrected = new Vector3();

	// velocities
	private Vector3 leftFootVelocity = new Vector3();
	private float leftFootVelocityMagnitude = 0;
	private Vector3 rightFootVelocity = new Vector3();
	private float rightFootVelocityMagnitude = 0;
	private float leftFootAngleDiff = 0;
	private float rightFootAngleDiff = 0;

	// acceleration
	private Vector3 leftFootAcceleration = new Vector3();
	private float leftFootAccelerationMagnitude = 0;
	private Vector3 rightFootAcceleration = new Vector3();
	private float rightFootAccelerationMagnitude = 0;

	// other data
	private long timeOfFrame = System.nanoTime();
	private LegTweakBuffer parent = null; // frame before this one
	private int frameNumber = 0; // higher number is older frame
	private int detectionMode = ANKLE_ACCEL; // detection mode
	private boolean accelerationAboveThresholdLeft = true;
	private boolean accelerationAboveThresholdRight = true;
	private Vector3 centerOfMass = new Vector3();
	private Vector3 centerOfMassVelocity = new Vector3();
	private Vector3 centerOfMassAcceleration = new Vector3();
	private float leftFloorLevel;
	private float rightFloorLevel;

	// hyperparameters
	public static final float SKATING_DISTANCE_CUTOFF = 0.5f;
	static float SKATING_VELOCITY_THRESHOLD = 2.6f;
	static float SKATING_ACCELERATION_THRESHOLD = 0.8f;
	private static final float SKATING_ROTVELOCITY_THRESHOLD = 4.5f;
	private static final float SKATING_LOCK_ENGAGE_PERCENT = 0.85f;
	private static final float SKATING_ACCELERATION_Y_USE_PERCENT = 0.25f;
	private static final float FLOOR_DISTANCE_CUTOFF = 0.125f;
	private static final float SIX_TRACKER_TOLLERANCE = -0.10f;
	private static final Vector3 FORCE_VECTOR_TO_PRESSURE = new Vector3(0.25f, 1.0f, 0.25f);
	private static final float FORCE_ERROR_TOLLERANCE = 4.0f;
	private static final float[] FORCE_VECTOR_FALLBACK = new float[] { 0.1f, 0.1f };

	static float PARAM_SCALAR_MAX = 3.2f;
	static float PARAM_SCALAR_MIN = 0.25f;
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
	public Vector3 getLeftFootPosition(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(leftFootPosition);
	}

	public void setLeftFootPosition(Vector3 leftFootPosition) {
		this.leftFootPosition = leftFootPosition;
	}

	public Vector3 getRightFootPosition() {
		return rightFootPosition;
	}

	public void setRightFootPosition(Vector3 rightFootPosition) {
		this.rightFootPosition.set(rightFootPosition);
	}

	public Vector3 getLeftKneePosition() {
		return leftKneePosition;
	}

	public void setLeftKneePosition(Vector3 leftKneePosition) {
		this.leftKneePosition.set(leftKneePosition);
	}

	public Vector3 getRightKneePosition(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();
		return vec.set(rightKneePosition);
	}

	public void setRightKneePosition(Vector3 rightKneePosition) {
		this.rightKneePosition.set(rightKneePosition);
	}

	public Vector3 getWaistPosition(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(waistPosition);
	}

	public void setWaistPosition(Vector3 waistPosition) {
		this.waistPosition.set(waistPosition);
	}

	public Quaternion getLeftFootRotation(Quaternion quat) {
		if (quat == null)
			quat = new Quaternion();

		return quat.set(leftFootRotation);
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation.set(leftFootRotation);
	}

	public Quaternion getRightFootRotation(Quaternion quat) {
		if (quat == null)
			quat = new Quaternion();

		return quat.set(rightFootRotation);
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation.set(rightFootRotation);
	}

	public Vector3 getLeftFootPositionCorrected(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(leftFootPositionCorrected);
	}

	public void setLeftFootPositionCorrected(Vector3 leftFootPositionCorrected) {
		this.leftFootPositionCorrected.set(leftFootPositionCorrected);
	}

	public Vector3 getRightFootPositionCorrected(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(rightFootPositionCorrected);
	}

	public void setRightFootPositionCorrected(Vector3 rightFootPositionCorrected) {
		this.rightFootPositionCorrected.set(rightFootPositionCorrected);
	}

	public Vector3 getLeftKneePositionCorrected(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(leftKneePositionCorrected);
	}

	public void setLeftKneePositionCorrected(Vector3 leftKneePositionCorrected) {
		this.leftKneePositionCorrected.set(leftKneePositionCorrected);
	}

	public Vector3 getRightKneePositionCorrected(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(rightKneePositionCorrected);
	}

	public void setRightKneePositionCorrected(Vector3 rightKneePositionCorrected) {
		this.rightKneePositionCorrected.set(rightKneePositionCorrected);
	}

	public Vector3 getWaistPositionCorrected(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(waistPositionCorrected);
	}

	public void setWaistPositionCorrected(Vector3 waistPositionCorrected) {
		this.waistPositionCorrected.set(waistPositionCorrected);
	}

	public Vector3 getLeftFootVelocity(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(leftFootVelocity);
	}

	public Vector3 getRightFootVelocity(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(rightFootVelocity);
	}

	public Vector3 getCenterOfMass(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(centerOfMass);
	}

	public void setCenterOfMass(Vector3 centerOfMass) {
		this.centerOfMass.set(centerOfMass);
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

	public void setLeftFootAcceleration(Vector3 leftFootAcceleration) {
		this.leftFootAcceleration.set(leftFootAcceleration);
	}

	public void setRightFootAcceleration(Vector3 rightFootAcceleration) {
		this.rightFootAcceleration.set(rightFootAcceleration);
	}

	public Vector3 getLeftFootAcceleration(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

		return vec.set(leftFootAcceleration);
	}

	public Vector3 getRightFootAcceleration(Vector3 vec) {
		if (vec == null)
			vec = new Vector3();

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

	// update the frame number and discard frames older than BUFFER_LEN
	public void updateFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;

		if (this.frameNumber >= BUFFER_LEN)
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
				|| leftFootPosition.getY() > leftFloorLevel + FLOOR_DISTANCE_CUTOFF
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
					|| rightFootPosition.getY() > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
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
				|| rightFootPosition.getY() > rightFloorLevel + FLOOR_DISTANCE_CUTOFF
				|| accelerationAboveThresholdRight
		) {
			return UNLOCKED;
		}

		return LOCKED;
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet disregarding vertical displacment
	private float getLeftFootHorizantalDifference() {
		return leftFootPositionCorrected.minus(leftFootPosition).setY(0).length();
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet
	private float getRightFootHorizantalDifference() {
		return rightFootPositionCorrected.minus(rightFootPosition).setY(0).length();
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

		leftFootVelocity = leftFootPosition.minus(parent.leftFootPosition);
		leftFootVelocityMagnitude = leftFootVelocity.len();
		rightFootVelocity = rightFootPosition.minus(parent.rightFootPosition);
		rightFootVelocityMagnitude = rightFootVelocity.len();
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
			.setY(leftFootAcceleration.getY() * SKATING_ACCELERATION_Y_USE_PERCENT)
			.length();

		rightFootAccelerationMagnitude = rightFootAcceleration
			.setY(rightFootAcceleration.getY() * SKATING_ACCELERATION_Y_USE_PERCENT)
			.length();
	}

	// compute the velocity and acceleration of the center of mass
	private void computeComAtributes() {
		centerOfMassVelocity = centerOfMass.minus(parent.centerOfMass);
		centerOfMassAcceleration = centerOfMassVelocity.minus(parent.centerOfMassVelocity);
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
			* FastMath.clamp(pressureScalars[0] * 2.0f, PRESSURE_SCALER_MIN, PRESSURE_SCALER_MAX);
		rightFootSensitivityVel = (rightFootScalarAccel
			+ rightFootScalarVel / 2.0f)
			* FastMath.clamp(pressureScalars[1] * 2.0f, PRESSURE_SCALER_MIN, PRESSURE_SCALER_MAX);

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
			Vector3 velocityDiff = leftFootVelocity.minus(rightFootVelocity);
			velocityDiff.setY(0.0f);
			float velocityDiffMagnitude = velocityDiff.len();

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
			Vector3 velocityDiff = rightFootVelocity.minus(leftFootVelocity);
			velocityDiff.setY(0.0f);
			float velocityDiffMagnitude = velocityDiff.len();

			if (velocityDiffMagnitude < MAX_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX;
			} else if (velocityDiffMagnitude > MIN_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX
					* (velocityDiffMagnitude - MIN_SCALAR_DORMANT)
					/ (MAX_SCALAR_DORMANT - MIN_SCALAR_DORMANT);
			}
		}

		// calculate the 'unlockedness factor' and use that to
		// determine the scalar (go as low as 0.5 as high as
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

		// get the vectors from the com to each foot
		Vector3 leftFootVector = leftFootPosition.minus(centerOfMass).unit();
		Vector3 rightFootVector = rightFootPosition.minus(centerOfMass).unit();

		// get the magnitude of the force on each foot
		float leftFootMagnitude = GRAVITY_MAGNITUDE * leftFootVector.getY() / leftFootVector.len();
		float rightFootMagnitude = GRAVITY_MAGNITUDE * rightFootVector.getY() / rightFootVector.len();

		// get the force vector each foot could apply to the com
		Vector3 leftFootForce = leftFootVector.times(leftFootMagnitude / 2.0f);
		Vector3 rightFootForce = rightFootVector.times(rightFootMagnitude / 2.0f);

		// based off the acceleration of the com, get the force each foot is
		// likely applying (the expected force sum should be equal to
		// centerOfMassAcceleration since the mass is 1)
		findForceVectors(leftFootForce, rightFootForce);

		// see if the force vectors found a reasonable solution
		// if they did not we assume there is another force acting on the com
		// and fall back to a low pressure prediction
		if (detectOutsideForces(leftFootForce, rightFootForce))
			return FORCE_VECTOR_FALLBACK;

		// set the pressure to the force on each foot times the force to
		// pressure scalar
		leftFootPressure = leftFootForce.times(FORCE_VECTOR_TO_PRESSURE).length();
		rightFootPressure = rightFootForce.times(FORCE_VECTOR_TO_PRESSURE).length();

		// distance from the ground is a factor in the pressure
		// using the inverse of the distance to the ground scale the
		// pressure
		float leftDistance = (leftFootPosition.getY()> leftFloorLevel)
			? (leftFootPosition.getY() - leftFloorLevel)
			: LegTweaks.NEARLY_ZERO;
		leftFootPressure *= 1.0f / (leftDistance);
		float rightDistance = (rightFootPosition.getY() > rightFloorLevel)
			? (rightFootPosition.getY() - rightFloorLevel)
			: LegTweaks.NEARLY_ZERO;
		rightFootPressure *= 1.0f / (rightDistance);

		// normalize the pressure values
		float pressureSum = leftFootPressure + rightFootPressure;
		leftFootPressure /= pressureSum;
		rightFootPressure /= pressureSum;

		return new float[] { leftFootPressure, rightFootPressure };
	}

	// perform a gradient descent to find the force vectors that best match the
	// acceleration of the com
	private void findForceVectors(Vector3 leftFootForce, Vector3 rightFootForce) {
		int iterations = 100;
		float stepSize = 0.01f;
		// set up the temporary variables
		Vector3 tempLeftFootForce1 = leftFootForce.clone();
		Vector3 tempLeftFootForce2 = leftFootForce.clone();
		Vector3 tempRightFootForce1 = rightFootForce.clone();
		Vector3 tempRightFootForce2 = rightFootForce.clone();
		Vector3 error;
		Vector3 error1;
		Vector3 error2;
		Vector3 error3;
		Vector3 error4;

		for (int i = 0; i < iterations; i++) {
			tempLeftFootForce1.set(leftFootForce);
			tempLeftFootForce2.set(leftFootForce);
			tempRightFootForce1.set(rightFootForce);
			tempRightFootForce2.set(rightFootForce);

			// get the error at the current position
			error = centerOfMassAcceleration
				.subtract(leftFootForce.add(rightFootForce).add(GRAVITY));

			// add and subtract the error to the force vectors
			tempLeftFootForce1 = tempLeftFootForce1.times(1.0f + stepSize);
			tempLeftFootForce2 = tempLeftFootForce2.times(1.0f - stepSize);
			tempRightFootForce1 = tempRightFootForce1.times(1.0f + stepSize);
			tempRightFootForce2 = tempRightFootForce2.times(1.0f - stepSize);

			// get the error at the new position
			error1 = getForceVectorError(tempLeftFootForce1, rightFootForce);
			error2 = getForceVectorError(tempLeftFootForce2, rightFootForce);
			error3 = getForceVectorError(tempRightFootForce1, leftFootForce);
			error4 = getForceVectorError(tempRightFootForce2, leftFootForce);

			// set the new force vectors
			if (error1.len() < error.len()) {
				leftFootForce.set(tempLeftFootForce1);
			} else if (error2.len() < error.len()) {
				leftFootForce.set(tempLeftFootForce2);
			}

			if (error3.len() < error.len()) {
				rightFootForce.set(tempRightFootForce1);
			} else if (error4.len() < error.len()) {
				rightFootForce.set(tempRightFootForce2);
			}
		}
	}

	// detect any outside forces on the body such
	// as a wall or a chair. returns true if there is a outside force
	private boolean detectOutsideForces(Vector3 f1, Vector3 f2) {
		Vector3 force = GRAVITY.plus(f1).plus(f2);
		Vector3 error = centerOfMassAcceleration.minus(force);
		return error.lenSq() > FastMath.sqr(FORCE_ERROR_TOLLERANCE);
	}

	// simple error function for the force vector gradient descent
	private Vector3 getForceVectorError(Vector3 testForce, Vector3 otherForce) {
		return centerOfMassAcceleration
			.minus(testForce.plus(otherForce).plus(GRAVITY));
	}
}
