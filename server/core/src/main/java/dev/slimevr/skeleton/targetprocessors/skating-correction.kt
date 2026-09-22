package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.COMState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.skeleton.computeComState
import dev.slimevr.skeleton.predictFootPressure
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.math.abs

data class LockState(
	val locked: Boolean,
	val position: Vector3 = Vector3.ZERO,
)

const val SKATING_LOCK_ENGAGE_PERCENT = 1.1f

// TODO These were squared for performance, but I think that requires squaring
//  everything else, and obscures the actual values. We need a better way of doing so if
//  we want to still do that. - Butterscotch
const val SKATING_DISTANCE_THRESHOLD = 0.5f
const val SKATING_ANGULAR_VELOCITY_THRESHOLD = 4.5f
const val SKATING_LINEAR_VELOCITY_THRESHOLD = 2.4f
const val SKATING_ACCELERATION_THRESHOLD = 0.7f

const val FLOOR_CALIBRATION_OFFSET = 0.0025f
const val FLOOR_DISTANCE_THRESHOLD = 0.065f

const val PARAM_SCALAR_MAX = 3.2f
const val PARAM_SCALAR_MIN = 0.25f
const val PARAM_SCALAR_MID = 1.0f

// The point at which the scalar is at the max or min depending on accel
const val MAX_SCALAR_ACCEL = 0.2f
const val MIN_SCALAR_ACCEL = 0.9f

// The point at which the scalar is at it max or min in a double locked foot situation
const val MAX_SCALAR_DORMANT = 0.2f
const val MIN_SCALAR_DORMANT = 1.50f

// The point at which the scalar is at it max or min in a single locked foot situation
const val MIN_SCALAR_ACTIVE = 1.75f
const val MAX_SCALAR_ACTIVE = 0.1f

// Maximum scalars for the pressure on each foot
const val PRESSURE_SCALAR_MIN = 0.1f
const val PRESSURE_SCALAR_MAX = 1.9f

fun shouldLock(
	position: Vector3,
	lastPosition: Vector3,
	acceleration: Vector3,
	velocity: Velocity,
	wasLocked: Boolean,
	floorLevel: Float = 0f,
	correctionStrength: Float = 1f,
	velocitySensitivity: Float = 1f,
	accelerationSensitivity: Float = 1f,
): Boolean {
	val thresholdMultiplier = (if (wasLocked) 1f else SKATING_LOCK_ENGAGE_PERCENT) * (correctionStrength * 0.5f + 0.5f)
	val floorLevel = floorLevel + FLOOR_CALIBRATION_OFFSET
	return ((position - lastPosition).let { Vector3(it.x, 0f, it.z) }.len() <= SKATING_DISTANCE_THRESHOLD) &&
		(velocity.linear.len() <= SKATING_LINEAR_VELOCITY_THRESHOLD * thresholdMultiplier * velocitySensitivity) &&
		(velocity.angular.len() <= SKATING_ANGULAR_VELOCITY_THRESHOLD * thresholdMultiplier * velocitySensitivity) &&
		(position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD) &&
		(acceleration.len() <= SKATING_ACCELERATION_THRESHOLD * thresholdMultiplier * correctionStrength * accelerationSensitivity)
}

fun computeLockState(
	wasLocked: Boolean,
	isLocked: Boolean,
	position: Vector3,
): LockState? = if (isLocked && !wasLocked) {
	LockState(
		true,
		position,
	)
} else if (!isLocked && wasLocked) {
	// Last locked position could be retained if needed, but I can't think of a use
	LockState(
		false,
	)
} else {
	null
}

fun computeSensitivity(
	isLocked: Boolean,
	altIsLocked: Boolean,
	accelerationMagnitude: Float,
	velocity: Vector3,
	altVelocity: Vector3,
	pressure: Float,
): Pair<Float, Float> {
	// Get the first set of scalars that are based on acceleration from the IMUs
	val footScalarAccel: Float = getFootScalarAccel(
		isLocked,
		accelerationMagnitude,
	)

	// Get the second set of scalars that is based off of how close each foot is to a
	//  lock and dynamically adjusting the scalars (based off the assumption that if you
	//  are standing one foot is likely planted on the ground unless you are moving
	//  fast)
	// TODO Always 3.2?
	val footScalarVel: Float = getFootLockLikelihood(
		velocity,
		altVelocity,
		isLocked && altIsLocked,
	)

	// Combine the scalars to get the final scalars
	val footSensitivityVel = (
		(
			footScalarAccel +
				footScalarVel /
				2f
			) *
			(pressure * 2f).coerceIn(
				PRESSURE_SCALAR_MIN,
				PRESSURE_SCALAR_MAX,
			)
		)

	// Velocity sensitivity to acceleration sensitivity
	return footSensitivityVel to footScalarVel
}

// Calculate a scalar using acceleration to apply to the non acceleration based
// 	hyperparameters when calculating lock states
fun getFootScalarAccel(
	isLocked: Boolean,
	accelerationMagnitude: Float,
): Float {
	if (isLocked) {
		if (accelerationMagnitude < MAX_SCALAR_ACCEL) {
			return PARAM_SCALAR_MAX
		} else if (accelerationMagnitude > MIN_SCALAR_ACCEL) {
			return (
				PARAM_SCALAR_MAX
					*
					(accelerationMagnitude - MIN_SCALAR_ACCEL) /
					(MAX_SCALAR_ACCEL - MIN_SCALAR_ACCEL)
				)
		}
	}
	return PARAM_SCALAR_MID
}

// Calculate a scalar using the velocity of the foot trackers and the lock states to
//  calculate a scalar to apply to the non acceleration based hyperparameters when
//  calculating lock states
fun getFootLockLikelihood(
	primaryFootVel: Vector3,
	otherFootVel: Vector3,
	bothLocked: Boolean,
): Float {
	if (bothLocked) {
		var velocityDiff: Vector3 = primaryFootVel - otherFootVel
		velocityDiff = Vector3(velocityDiff.x, 0f, velocityDiff.z)
		val velocityDiffMagnitude: Float = velocityDiff.len()
		if (velocityDiffMagnitude < MAX_SCALAR_DORMANT) {
			return PARAM_SCALAR_MAX
		} else if (velocityDiffMagnitude > MIN_SCALAR_DORMANT) {
			return (
				PARAM_SCALAR_MAX
					*
					(velocityDiffMagnitude - MIN_SCALAR_DORMANT) /
					(MAX_SCALAR_DORMANT - MIN_SCALAR_DORMANT)
				)
		}
	}

	// Calculate the 'unlockedness factor' and use that to determine the scalar (go as
	// 	low as 0.5 and as high as param_scalar_max)
	val velocityDiffAbs: Float = abs(primaryFootVel.len() - otherFootVel.len())
	if (velocityDiffAbs > MIN_SCALAR_ACTIVE) {
		return PARAM_SCALAR_MIN
	} else if (velocityDiffAbs < MAX_SCALAR_ACTIVE) {
		return PARAM_SCALAR_MAX
	}
	return (
		PARAM_SCALAR_MAX
			*
			(velocityDiffAbs - MIN_SCALAR_ACTIVE) /
			(MAX_SCALAR_ACTIVE - MIN_SCALAR_ACTIVE) -
			PARAM_SCALAR_MID
		)
}

class SkatingCorrectionTargetProcessor(val settings: Settings) :
	SkeletonTargetProcessor,
	ResettableSkeletonProcessor {
	val skatingBodyParts = arrayOf(
		BodyPart.LEFT_LOWER_LEG to BodyPart.RIGHT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG to BodyPart.LEFT_LOWER_LEG,
	)

	// Centre of mass
	var comState: COMState? = null

	val pressure: BodyPartMap<Float> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()

	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.skatingCorrection) return

		// Update centre of mass
		val comState = computeComState(
			timeSource.markNow(),
			comState,
			centreOfMass(fk),
		)
		this.comState = comState

		// TODO Clean up whatever the hell this is
		// Predict the pressure for each foot
		val (leftPressure, rightPressure) = predictFootPressure(
			fk[BodyPart.LEFT_LOWER_LEG]?.tailPosition ?: return,
			fk[BodyPart.RIGHT_LOWER_LEG]?.tailPosition ?: return,
			comState.position,
			comState.acceleration,
			floorLevel,
		)
		pressure[BodyPart.LEFT_LOWER_LEG] = leftPressure
		pressure[BodyPart.RIGHT_LOWER_LEG] = rightPressure

		val correctionStrength = skeletonConfig.ratios.skatingCorrectionStrength

		for ((bodyPart, altBodyPart) in skatingBodyParts) {
			val locked = lockState[bodyPart]?.locked ?: false
			val altLocked = lockState[altBodyPart]?.locked ?: false

			val bone = fk[bodyPart] ?: return
			val altBone = fk[bodyPart] ?: return

			val (velocitySensitivity, accelerationSensitivity) = computeSensitivity(
				locked,
				altLocked,
				bone.acceleration.len(),
				bone.velocity.linear,
				altBone.velocity.linear,
				// TODO Do something better
				pressure[bodyPart] ?: 0.1f,
			)

			val curPosition = bone.tailPosition

			// Consider locking BodyPart
			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked == true
			val isLocked = shouldLock(
				curPosition,
				if (wasLocked) {
					lastState.position
				} else {
					curPosition
				},
				bone.acceleration,
				bone.velocity,
				wasLocked,
				floorLevel,
				correctionStrength,
				velocitySensitivity,
				accelerationSensitivity,
			)

			val activeState = computeLockState(
				wasLocked,
				isLocked,
				curPosition,
			)?.also {
				// Track lock state changes
				lockState[bodyPart] = it
				// Otherwise pull the last state
			} ?: lastState ?: continue

			if (activeState.locked) {
				mutableIkTargets[bodyPart] = activeState.position
			}
		}
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			comState = null
			lockState.clear()
		}
	}
}
