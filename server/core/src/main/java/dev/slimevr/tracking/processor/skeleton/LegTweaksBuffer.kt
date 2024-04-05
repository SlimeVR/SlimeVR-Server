package dev.slimevr.tracking.processor.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

/**
 * class that holds data related to the state and other various attributes of
 * the legs such as the position of the foot, knee, and hip, after and before
 * correction, the velocity of the foot and the computed state of the feet at
 * that frame. mainly calculates the state of the legs per frame using these
 * rules: The conditions for an unlock are as follows: 1. the foot is too far
 * from its correct position 2. a velocity higher than a threshold is achieved
 * 3. a large acceleration is applied to the foot 4. angular velocity of the
 * foot goes higher than a threshold. The conditions for a lock are the opposite
 * of the above but require a lower value for all of the above conditions. The
 * aforementioned thresholds are computed by applying scalars to a base
 * threshold value. This allows one set of initial values to be applicable to a
 * large range of actions and body types.
 */

class LegTweaksBuffer @Suppress("ktlint") constructor() {
	// hyperparameters / constants
	companion object {
		const val STATE_UNKNOWN = 0
		const val LOCKED = 1
		const val UNLOCKED = 2
		const val FOOT_ACCEL = 3
		const val ANKLE_ACCEL = 4
		const val NS_CONVERT = 1.0e9f
		const val BUFFER_LEN = 10
		val GRAVITY: Vector3 = Vector3(0f, -9.81f, 0f)
		val GRAVITY_MAGNITUDE: Float = GRAVITY.len()

		private const val SKATING_DISTANCE_CUTOFF = 0.5f
		private const val SKATING_ROTVELOCITY_THRESHOLD = 4.5f
		private const val SKATING_LOCK_ENGAGE_PERCENT = 1.1f
		private const val FLOOR_DISTANCE_CUTOFF = 0.065f
		private const val SIX_TRACKER_TOLERANCE = -0.10f
		private val FORCE_VECTOR_TO_PRESSURE: Vector3 = Vector3(0.25f, 1.0f, 0.25f)
		private val FORCE_ERROR_TOLERANCE_SQR: Float = 4.0f.pow(2)
		private val FORCE_VECTOR_FALLBACK = floatArrayOf(0.1f, 0.1f)

		var SKATING_VELOCITY_THRESHOLD = 2.4f
		var SKATING_ACCELERATION_THRESHOLD = 0.8f

		private var PARAM_SCALAR_MAX = 3.2f
		private var PARAM_SCALAR_MIN = 0.25f
		private const val PARAM_SCALAR_MID = 1.0f

		// the point at which the scalar is at the max or min depending on accel
		private const val MAX_SCALAR_ACCEL = 0.2f
		private const val MIN_SCALAR_ACCEL = 0.9f

		// the point at which the scalar is at it max or min in a double locked foot
		// situation
		private const val MAX_SCALAR_DORMANT = 0.2f
		private const val MIN_SCALAR_DORMANT = 1.50f

		// the point at which the scalar is at it max or min in a single locked foot
		// situation
		private const val MIN_SCALAR_ACTIVE = 1.75f
		private const val MAX_SCALAR_ACTIVE = 0.1f

		// maximum scalars for the pressure on each foot
		private const val PRESSURE_SCALAR_MIN = 0.1f
		private const val PRESSURE_SCALAR_MAX = 1.9f

		private const val SKATING_CUTOFF_ENGAGE = (
			SKATING_DISTANCE_CUTOFF
				* SKATING_LOCK_ENGAGE_PERCENT
			)
		private var SKATING_VELOCITY_CUTOFF_ENGAGE = (
			SKATING_VELOCITY_THRESHOLD
				* SKATING_LOCK_ENGAGE_PERCENT
			)
		private var SKATING_ACCELERATION_CUTOFF_ENGAGE = (
			SKATING_ACCELERATION_THRESHOLD
				* SKATING_LOCK_ENGAGE_PERCENT
			)
		private const val SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE =
			(
				SKATING_ROTVELOCITY_THRESHOLD
					* SKATING_LOCK_ENGAGE_PERCENT
				)

		fun setSkatingVelocityThreshold(value: Float) {
			SKATING_VELOCITY_THRESHOLD = value
			SKATING_VELOCITY_CUTOFF_ENGAGE = (
				SKATING_VELOCITY_THRESHOLD
					* SKATING_LOCK_ENGAGE_PERCENT
				)
		}

		fun getSkatingVelocityThreshold(): Float = SKATING_VELOCITY_THRESHOLD

		fun setSkatingAccelerationThreshold(value: Float) {
			SKATING_ACCELERATION_THRESHOLD = value
			SKATING_ACCELERATION_CUTOFF_ENGAGE = (
				SKATING_ACCELERATION_THRESHOLD
					* SKATING_LOCK_ENGAGE_PERCENT
				)
		}

		fun getSkatingAccelerationThreshold(): Float = SKATING_ACCELERATION_THRESHOLD
	}

	// states for the legs
	var leftLegState = STATE_UNKNOWN
	var rightLegState = STATE_UNKNOWN
	var leftLegNumericalState = 0f
		private set
	var rightLegNumericalState = 0f
		private set

	// positions and rotations
	var leftFootPosition: Vector3 = Vector3.NULL
		private set
	var rightFootPosition: Vector3 = Vector3.NULL
		private set
	var leftKneePosition: Vector3 = Vector3.NULL
		private set
	var rightKneePosition: Vector3 = Vector3.NULL
		private set
	var hipPosition: Vector3 = Vector3.NULL
		private set
	var leftFootRotation: Quaternion = Quaternion.IDENTITY
		private set
	var rightFootRotation: Quaternion = Quaternion.IDENTITY
		private set
	var leftFootPositionCorrected: Vector3 = Vector3.NULL
		private set
	var rightFootPositionCorrected: Vector3 = Vector3.NULL
		private set
	var leftKneePositionCorrected: Vector3 = Vector3.NULL
		private set
	var rightKneePositionCorrected: Vector3 = Vector3.NULL
		private set
	var hipPositionCorrected: Vector3 = Vector3.NULL
		private set
	var leftFootRotationCorrected: Quaternion =
		Quaternion.IDENTITY
		private set
	var rightFootRotationCorrected: Quaternion =
		Quaternion.IDENTITY
		private set

	// velocities
	var leftFootVelocity: Vector3 = Vector3.NULL
		private set
	var leftFootVelocityMagnitude = 0f
		private set
	var rightFootVelocity: Vector3 = Vector3.NULL
		private set
	var rightFootVelocityMagnitude = 0f
		private set
	private var leftFootAngleDiff = 0f
	private var rightFootAngleDiff = 0f

	// acceleration
	var leftFootAcceleration: Vector3 = Vector3.NULL
		private set
	var leftFootAccelerationMagnitude = 0f
		private set
	var rightFootAcceleration: Vector3 = Vector3.NULL
		private set
	var rightFootAccelerationMagnitude = 0f
		private set

	// center of mass
	var centerOfMass: Vector3 = Vector3.NULL
		private set
	var centerOfMassVelocity: Vector3 = Vector3.NULL
		private set
	var centerOfMassAcceleration: Vector3 = Vector3.NULL
		private set

	// other data
	val timeOfFrame: Long = System.nanoTime()
	var parent: LegTweaksBuffer? = null
		private set

	private var frameNumber = 0 // higher number is older frame
	private var detectionMode = ANKLE_ACCEL
	private var accelerationAboveThresholdLeft = true
	private var accelerationAboveThresholdRight = true
	private var leftFloorLevel = 0f
	private var rightFloorLevel = 0f
	var isStanding = false
		private set

	private var leftFootSensitivityVel = 1.0f
	private var rightFootSensitivityVel = 1.0f
	private var leftFootSensitivityAccel = 1.0f
	private var rightFootSensitivityAccel = 1.0f

	constructor(
		leftFootPosition: Vector3,
		rightFootPosition: Vector3,
		leftKneePosition: Vector3,
		rightKneePosition: Vector3,
		leftFootRotation: Quaternion,
		rightFootRotation: Quaternion,
		leftFloorLevel: Float,
		rightFloorLevel: Float,
		leftFootAcceleration: Vector3,
		rightFootAcceleration: Vector3,
		accelerationMode: Int,
		hipPosition: Vector3,
		centerOfMass: Vector3,
		parent: LegTweaksBuffer,
		active: Boolean,
	) : this() {
		this.leftFootPosition = leftFootPosition
		this.rightFootPosition = rightFootPosition
		this.leftKneePosition = leftKneePosition
		this.rightKneePosition = rightKneePosition
		this.leftFootRotation = leftFootRotation
		this.rightFootRotation = rightFootRotation
		this.leftFloorLevel = leftFloorLevel
		this.rightFloorLevel = rightFloorLevel
		this.leftFootAcceleration = leftFootAcceleration
		this.rightFootAcceleration = rightFootAcceleration
		this.detectionMode = accelerationMode
		this.hipPosition = hipPosition
		this.centerOfMass = centerOfMass
		this.parent = parent

		this.calculateFootAttributes(active)
	}

	fun setPositions(
		leftFootPosition: Vector3,
		rightFootPosition: Vector3,
		leftKneePosition: Vector3,
		rightKneePosition: Vector3,
		hipPosition: Vector3,
	) {
		this.leftFootPosition = leftFootPosition
		this.rightFootPosition = rightFootPosition
		this.leftKneePosition = leftKneePosition
		this.rightKneePosition = rightKneePosition
		this.hipPosition = hipPosition
	}

	fun setCorrectedPositions(
		leftFootPosition: Vector3,
		rightFootPosition: Vector3,
		leftKneePosition: Vector3,
		rightKneePosition: Vector3,
		hipPosition: Vector3,
	) {
		this.leftFootPositionCorrected = leftFootPosition
		this.rightFootPositionCorrected = rightFootPosition
		this.leftKneePositionCorrected = leftKneePosition
		this.rightKneePositionCorrected = rightKneePosition
		this.hipPositionCorrected = hipPosition
	}

	fun setCorrectedRotations(leftFootRotation: Quaternion, rightFootRotation: Quaternion) {
		this.leftFootRotationCorrected = leftFootRotation
		this.rightFootRotationCorrected = rightFootRotation
	}

	// returns 1 / delta time
	fun getTimeDelta(): Float = if (parent == null) 0.0f else 1.0f / ((timeOfFrame - parent!!.timeOfFrame) / NS_CONVERT)

	// calculate movement attributes
	private fun calculateFootAttributes(active: Boolean) {
		updateFrameNumber(0)

		// compute attributes of the legs
		computeVelocity()
		computeAccelerationMagnitude()
		computeComAttributes()

		// check if the acceleration triggers forced unlock
		if (detectionMode == FOOT_ACCEL) {
			computeAccelerationAboveThresholdFootTrackers()
		} else {
			computeAccelerationAboveThresholdAnkleTrackers()
		}

		// calculate the scalar for other parameters
		computeScalar()

		// if correction is inactive state is unknown (default to unlocked)
		if (!active || parent == null) {
			leftLegState = UNLOCKED
			rightLegState = UNLOCKED
		} else {
			computeState()
		}
	}

	// update the frame number and discard frames older than BUFFER_LEN
	private fun updateFrameNumber(frameNumber: Int) {
		this.frameNumber = frameNumber
		if (this.frameNumber >= BUFFER_LEN) parent = null
		parent?.updateFrameNumber(frameNumber + 1)
	}

	// compute the state of the legs
	private fun computeState() {
		// get the difference of the corrected and current positions
		val leftDiff = getFootHorizontalDifference(parent!!.leftFootPositionCorrected, leftFootPosition)
		val rightDiff = getFootHorizontalDifference(parent!!.rightFootPositionCorrected, rightFootPosition)

		// based on the last state of the legs compute their state for this
		// individual frame
		leftLegState = checkState(
			parent!!.leftLegState,
			leftDiff,
			leftFootVelocityMagnitude,
			leftFootSensitivityVel,
			leftFootAngleDiff,
			leftFloorLevel,
			accelerationAboveThresholdLeft,
			leftFootPosition,
		)
		rightLegState = checkState(
			parent!!.rightLegState,
			rightDiff,
			rightFootVelocityMagnitude,
			rightFootSensitivityVel,
			rightFootAngleDiff,
			rightFloorLevel,
			accelerationAboveThresholdRight,
			rightFootPosition,
		)

		computeNumericalState()
	}

	// check if a locked foot should stay locked or be released and vice versa
	private fun checkState(
		legState: Int,
		horizontalDifference: Float,
		velocityMagnitude: Float,
		velocitySensitivity: Float,
		angleDiff: Float,
		floorLevel: Float,
		accelerationAboveThreshold: Boolean,
		footPosition: Vector3,
	): Int {
		val timeStep = getTimeDelta()
		if (legState == UNLOCKED) {
			return if (horizontalDifference > SKATING_CUTOFF_ENGAGE ||
				(velocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE * velocitySensitivity) ||
				(angleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE * velocitySensitivity) ||
				footPosition.y > floorLevel + FLOOR_DISTANCE_CUTOFF ||
				accelerationAboveThreshold
			) {
				UNLOCKED
			} else {
				LOCKED
			}
		}
		return if (horizontalDifference > SKATING_DISTANCE_CUTOFF ||
			(velocityMagnitude * timeStep > SKATING_VELOCITY_THRESHOLD * velocitySensitivity) ||
			(angleDiff * timeStep > SKATING_ROTVELOCITY_THRESHOLD * velocitySensitivity) ||
			footPosition.y > floorLevel + FLOOR_DISTANCE_CUTOFF ||
			accelerationAboveThreshold
		) {
			UNLOCKED
		} else {
			LOCKED
		}
	}

	// compute a numerical value representing how locked a foot is (bigger
	// number == less locked)
	private fun computeNumericalState() {
		leftLegNumericalState = computeNumericalState(
			leftFootVelocityMagnitude,
			leftFootAccelerationMagnitude,
			leftFootSensitivityAccel,
			leftFootSensitivityVel,
		)
		rightLegNumericalState = computeNumericalState(
			rightFootVelocityMagnitude,
			rightFootAccelerationMagnitude,
			rightFootSensitivityAccel,
			rightFootSensitivityVel,
		)
	}

	// returns the average percentage the real velocity and acceleration are of
	// the scaled thresholds for velocity and acceleration
	private fun computeNumericalState(
		velocityMagnitude: Float,
		accelerationMagnitude: Float,
		accelSensitivity: Float,
		velSensitivity: Float,
	): Float {
		val timeStep = getTimeDelta()
		val velocity = velocityMagnitude * timeStep
		val velocityPercentage: Float =
			velocity / (SKATING_VELOCITY_THRESHOLD * velSensitivity)
		val accelerationPercentage: Float = (
			accelerationMagnitude /
				(SKATING_ACCELERATION_THRESHOLD * accelSensitivity)
			)
		return (velocityPercentage + accelerationPercentage) * 0.5f
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet disregarding vertical displacement
	private fun getFootHorizontalDifference(correctedPosition: Vector3, position: Vector3): Float {
		val diff: Vector3 = correctedPosition - position
		return Vector3(
			diff.x,
			0f,
			diff.z,
		).len()
	}

	// get the angular velocity of the left foot (kinda we just want a scalar)
	private fun getFootAngularVelocity(oldRot: Quaternion, rot: Quaternion): Float = (rot.toMatrix().z.unit() - oldRot.toMatrix().z.unit()).len()

	// compute the velocity of the feet from the position in the last frames
	private fun computeVelocity() {
		if (parent == null) return
		leftFootVelocity = leftFootPosition - parent!!.leftFootPosition
		leftFootVelocityMagnitude = leftFootVelocity.len()
		rightFootVelocity = rightFootPosition - parent!!.rightFootPosition
		rightFootVelocityMagnitude = rightFootVelocity.len()
		leftFootAngleDiff = getFootAngularVelocity(parent!!.leftFootRotation, leftFootRotation)
		rightFootAngleDiff = getFootAngularVelocity(parent!!.rightFootRotation, rightFootRotation)
	}

	// get the nth parent of this frame
	private fun getNParent(n: Int): LegTweaksBuffer? = if (n == 0 || parent == null) this else parent!!.getNParent(n - 1)

	// compute the acceleration magnitude of the feet from the acceleration
	// given by the imus
	private fun computeAccelerationMagnitude() {
		leftFootAccelerationMagnitude = leftFootAcceleration.len()
		rightFootAccelerationMagnitude = rightFootAcceleration.len()
	}

	// compute the velocity and acceleration of the center of mass
	private fun computeComAttributes() {
		centerOfMassVelocity = centerOfMass - parent!!.centerOfMass
		centerOfMassAcceleration = centerOfMassVelocity - parent!!.centerOfMassVelocity
	}

	// for a setup with foot trackers the data from the imus is enough to determine lock/unlock
	private fun computeAccelerationAboveThresholdFootTrackers() {
		accelerationAboveThresholdLeft = (
			leftFootAccelerationMagnitude
				> SKATING_ACCELERATION_CUTOFF_ENGAGE * leftFootSensitivityAccel
			)
		accelerationAboveThresholdRight = (
			rightFootAccelerationMagnitude
				> SKATING_ACCELERATION_CUTOFF_ENGAGE * rightFootSensitivityAccel
			)
	}

	// for any setup without foot trackers the data from the imus is enough to
	// determine lock/unlock but we add some tolerance
	private fun computeAccelerationAboveThresholdAnkleTrackers() {
		accelerationAboveThresholdLeft = (
			leftFootAccelerationMagnitude
				> (SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLERANCE) * leftFootSensitivityAccel
			)
		accelerationAboveThresholdRight = (
			rightFootAccelerationMagnitude
				> (SKATING_ACCELERATION_THRESHOLD + SIX_TRACKER_TOLERANCE) * rightFootSensitivityAccel
			)
	}

	private fun computeScalar() {
		// get the first set of scalars that are based on acceleration from the
		// imus
		val leftFootScalarAccel: Float = getFootScalarAccel(leftFootAccelerationMagnitude)
		val rightFootScalarAccel: Float = getFootScalarAccel(rightFootAccelerationMagnitude)

		// get the second set of scalars that is based off of how close each
		// foot is to a lock and dynamically adjusting the scalars
		// (based off the assumption that if you are standing one foot is likely
		// planted on the ground unless you are moving fast)
		val leftFootScalarVel: Float = getFootLockLikelihood(
			leftFootVelocity,
			rightFootVelocity,
			leftFootVelocityMagnitude,
			rightFootVelocityMagnitude,
		)
		val rightFootScalarVel: Float = getFootLockLikelihood(
			rightFootVelocity,
			leftFootVelocity,
			rightFootVelocityMagnitude,
			leftFootVelocityMagnitude,
		)

		// get the third set of scalars that is based on where the COM is
		val pressureScalars: FloatArray = getPressurePrediction()

		// combine the scalars to get the final scalars
		leftFootSensitivityVel = (
			(
				leftFootScalarAccel +
					leftFootScalarVel / 2.0f
				) *
				FastMath.clamp(
					pressureScalars[0] * 2.0f,
					PRESSURE_SCALAR_MIN,
					PRESSURE_SCALAR_MAX,
				)
			)
		rightFootSensitivityVel = (
			(
				rightFootScalarAccel +
					rightFootScalarVel / 2.0f
				) *
				FastMath.clamp(
					pressureScalars[1] * 2.0f,
					PRESSURE_SCALAR_MIN,
					PRESSURE_SCALAR_MAX,
				)
			)

		leftFootSensitivityAccel = leftFootScalarVel
		rightFootSensitivityAccel = rightFootScalarVel
	}

	// calculate a scalar using acceleration to apply to the non acceleration
	// based hyperparameters when calculating
	// lock states
	private fun getFootScalarAccel(accelMagnitude: Float): Float {
		if (leftLegState == LOCKED) {
			if (accelMagnitude < MAX_SCALAR_ACCEL) {
				return PARAM_SCALAR_MAX
			} else if (accelMagnitude > MIN_SCALAR_ACCEL) {
				return (
					PARAM_SCALAR_MAX
						* (accelMagnitude - MIN_SCALAR_ACCEL) /
						(MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL)
					)
			}
		}
		return PARAM_SCALAR_MID
	}

	// calculate a scalar using the velocity of the foot trackers and the lock
	// states to calculate a scalar to apply to the non acceleration based
	// hyperparameters when calculating
	// lock states
	private fun getFootLockLikelihood(
		primaryFootVel: Vector3,
		otherFootVel: Vector3,
		primaryFootVelMagnitude: Float,
		otherFootVelMagnitude: Float,
	): Float {
		if (leftLegState == LOCKED && rightLegState == LOCKED) {
			var velocityDiff: Vector3 = primaryFootVel - otherFootVel
			velocityDiff = Vector3(velocityDiff.x, 0f, velocityDiff.z)
			val velocityDiffMagnitude: Float = velocityDiff.len()
			if (velocityDiffMagnitude < MAX_SCALAR_DORMANT) {
				return PARAM_SCALAR_MAX
			} else if (velocityDiffMagnitude > MIN_SCALAR_DORMANT) {
				return (
					PARAM_SCALAR_MAX
						* (velocityDiffMagnitude - MIN_SCALAR_DORMANT) /
						(MAX_SCALAR_DORMANT - MIN_SCALAR_DORMANT)
					)
			}
		}

		// calculate the 'unlockedness factor' and use that to
		// determine the scalar (go as low as 0.5 and as high as
		// param_scalar_max)
		val velocityDiffAbs: Float = abs(primaryFootVelMagnitude - otherFootVelMagnitude)
		if (velocityDiffAbs > MIN_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MIN
		} else if (velocityDiffAbs < MAX_SCALAR_ACTIVE) {
			return PARAM_SCALAR_MAX
		}
		return (
			PARAM_SCALAR_MAX
				* (velocityDiffAbs - MIN_SCALAR_ACTIVE) /
				(MAX_SCALAR_ACTIVE - MIN_SCALAR_ACTIVE) -
				PARAM_SCALAR_MID
			)
	}

	// get the pressure prediction for the feet based of the center of mass
	// (assume mass is 1)
	// for understanding in the future this is assuming that the mass is one and
	// the force of gravity
	// is 9.8 m/s^2 this allows for the force sum to map directly to the
	// acceleration of the center of mass
	// since F = ma and if m is 1 then F = a
	private fun getPressurePrediction(): FloatArray {
		// get the vectors from the com to each foot
		val leftFootVector: Vector3 = (leftFootPosition - centerOfMass).unit()
		val rightFootVector: Vector3 = (rightFootPosition - centerOfMass).unit()

		// get the magnitude of the force on each foot
		val leftFootMagnitude: Float =
			GRAVITY_MAGNITUDE * leftFootVector.y / leftFootVector.len()
		val rightFootMagnitude: Float = (
			GRAVITY_MAGNITUDE
				* rightFootVector.y /
				rightFootVector.len()
			)

		// get the force vector each foot could apply to the com
		val leftFootForce: Vector3 = leftFootVector * (leftFootMagnitude / 2.0f)
		val rightFootForce: Vector3 = rightFootVector * (rightFootMagnitude / 2.0f)

		// based off the acceleration of the com, get the force each foot is
		// likely applying (the expected force sum should be equal to
		// centerOfMassAcceleration since the mass is 1)
		val (modifiedLeftFootForce, modifiedRightFootForce) =
			findForceVectors(leftFootForce, rightFootForce)

		// see if the force vectors found a reasonable solution
		// if they did not we assume there is another force acting on the com
		// and fall back to a low pressure prediction
		if (detectOutsideForces(modifiedLeftFootForce, modifiedRightFootForce)) {
			isStanding = false
			return FORCE_VECTOR_FALLBACK
		}
		isStanding = true

		// set the pressure to the force on each foot times the force to
		// pressure scalar
		var leftFootPressure = modifiedLeftFootForce.hadamard(FORCE_VECTOR_TO_PRESSURE).len()
		var rightFootPressure = modifiedRightFootForce.hadamard(FORCE_VECTOR_TO_PRESSURE).len()

		// distance from the ground is a factor in the pressure
		// using the inverse of the distance to the ground scale the
		// pressure
		val leftDistance: Float =
			if (leftFootPosition.y > (leftFloorLevel + FLOOR_DISTANCE_CUTOFF)) {
				leftFootPosition.y - (leftFloorLevel + FLOOR_DISTANCE_CUTOFF)
			} else {
				LegTweaks.NEARLY_ZERO
			}
		leftFootPressure *= 1.0f / leftDistance
		val rightDistance: Float =
			if (rightFootPosition.y > (rightFloorLevel + FLOOR_DISTANCE_CUTOFF)) {
				rightFootPosition.y - (rightFloorLevel + FLOOR_DISTANCE_CUTOFF)
			} else {
				LegTweaks.NEARLY_ZERO
			}
		rightFootPressure *= 1.0f / rightDistance

		// normalize the pressure values
		val pressureSum = leftFootPressure + rightFootPressure
		leftFootPressure /= pressureSum
		rightFootPressure /= pressureSum
		return floatArrayOf(leftFootPressure, rightFootPressure)
	}

	// perform a gradient descent to find the force vectors that best match the
	// acceleration of the com
	private fun findForceVectors(leftFootForceInit: Vector3, rightFootForceInit: Vector3): List<Vector3> {
		var leftFootForce: Vector3 = leftFootForceInit
		var rightFootForce: Vector3 = rightFootForceInit
		val iterations = 100
		val stepSize = 0.01f
		// set up the temporary variables
		var tempLeftFootForce1: Vector3
		var tempLeftFootForce2: Vector3
		var tempRightFootForce1: Vector3
		var tempRightFootForce2: Vector3
		var error: Vector3
		var error1: Vector3
		var error2: Vector3
		var error3: Vector3
		var error4: Vector3
		for (i in 0 until iterations) {
			tempLeftFootForce1 = leftFootForce
			tempLeftFootForce2 = leftFootForce
			tempRightFootForce1 = rightFootForce
			tempRightFootForce2 = rightFootForce

			// get the error at the current position
			error = centerOfMassAcceleration - (leftFootForce + rightFootForce + GRAVITY)

			// add and subtract the error to the force vectors
			tempLeftFootForce1 *= (1.0f + stepSize)
			tempLeftFootForce2 *= (1.0f - stepSize)
			tempRightFootForce1 *= (1.0f + stepSize)
			tempRightFootForce2 *= (1.0f - stepSize)

			// get the error at the new position
			error1 = getForceVectorError(tempLeftFootForce1, rightFootForce)
			error2 = getForceVectorError(tempLeftFootForce2, rightFootForce)
			error3 = getForceVectorError(tempRightFootForce1, leftFootForce)
			error4 = getForceVectorError(tempRightFootForce2, leftFootForce)

			// set the new force vectors
			if (error1.len() < error.len()) {
				leftFootForce = tempLeftFootForce1
			} else if (error2.len() < error.len()) {
				leftFootForce = tempLeftFootForce2
			}
			if (error3.len() < error.len()) {
				rightFootForce = tempRightFootForce1
			} else if (error4.len() < error.len()) {
				rightFootForce = tempRightFootForce2
			}
		}

		return listOf(leftFootForce, rightFootForce)
	}

	// detect any outside forces on the body such
	// as a wall or a chair. returns true if there is an outside force
	private fun detectOutsideForces(f1: Vector3, f2: Vector3): Boolean {
		val force: Vector3 = GRAVITY + f1 + f2
		val error: Vector3 = centerOfMassAcceleration - force
		return error.lenSq() > FORCE_ERROR_TOLERANCE_SQR
	}

	// simple error function for the force vector gradient descent
	private fun getForceVectorError(testForce: Vector3, otherForce: Vector3): Vector3 = centerOfMassAcceleration - (testForce + otherForce + GRAVITY)
}
