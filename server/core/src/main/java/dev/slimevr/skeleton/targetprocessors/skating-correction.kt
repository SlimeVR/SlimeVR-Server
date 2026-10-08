package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.COMState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.skeleton.computeComState
import dev.slimevr.skeleton.predictFootPressure
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.math.abs
import kotlin.time.ComparableTimeMark

data class LockState(
	val locked: Boolean,
	val position: Vector3 = Vector3.ZERO,
)

data class ReleaseSmoothing(
	val startTime: ComparableTimeMark,
	val duration: Float,
	val offset: Vector3,
)

const val SKATING_LOCK_ENGAGE_PERCENT = 1.1f

// TODO These were squared for performance, but I think that requires squaring
//  everything else, and obscures the actual values. We need a better way of doing so if
//  we want to still do that. - Butterscotch
const val SKATING_DISTANCE_THRESHOLD = 0.2f
const val SKATING_ANGULAR_VELOCITY_THRESHOLD = 4.5f
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

/**
 * The maximum time in seconds that it can take to reach the unlocked position from the
 * locked position.
 */
const val RELEASE_SMOOTHING_MAX_DURATION = 0.3f

/**
 * The distance that the max smoothing duration scales over (velocity of smoothing is
 * [RELEASE_SMOOTHING_MAX_DURATION] divided by [RELEASE_SMOOTHING_DISTANCE]).
 */
const val RELEASE_SMOOTHING_DISTANCE = 0.2f

fun shouldLock(
	position: Vector3,
	lastPosition: Vector3,
	acceleration: Vector3,
	angularVelocity: Vector3,
	wasLocked: Boolean,
	floorLevel: Float = 0f,
	correctionStrength: Float = 1f,
	velocitySensitivity: Float = 1f,
	accelerationSensitivity: Float = 1f,
): Boolean {
	val thresholdMultiplier = (if (wasLocked) 1f else SKATING_LOCK_ENGAGE_PERCENT) * (correctionStrength * 0.5f + 0.5f)
	val floorLevel = floorLevel + FLOOR_CALIBRATION_OFFSET
	return ((position - lastPosition).let { Vector3(it.x, 0f, it.z) }.len() <= SKATING_DISTANCE_THRESHOLD) &&
		(position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD) &&
		(angularVelocity.len() <= SKATING_ANGULAR_VELOCITY_THRESHOLD * thresholdMultiplier * velocitySensitivity) &&
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

/**
 * Calculates a duration to fit the release smoothing velocity.
 */
fun releaseSmoothingDuration(distance: Float): Float = (
	distance / RELEASE_SMOOTHING_DISTANCE
	).coerceAtMost(1f) *
	RELEASE_SMOOTHING_MAX_DURATION

fun releaseSmoothingMultiplier(
	time: ComparableTimeMark,
	smoothing: ReleaseSmoothing,
): Float = 1f - ((time - smoothing.startTime).inFloatingSeconds / smoothing.duration)

data class SkatingBodyParts(
	// ex. Left foot
	val bodyPart: BodyPart,
	// Mirror of bodyPart (ex. right foot)
	val mirrorBodyPart: BodyPart,
	// ex. Left lower leg (ankle)
	val ikTargetBodyPart: BodyPart,
)

class SkatingCorrectionTargetProcessor(val settings: Settings, val skeleton: Skeleton) :
	SkeletonTargetProcessor,
	ResettableSkeletonProcessor {
	val skatingBodyParts = arrayOf(
		SkatingBodyParts(
			BodyPart.LEFT_FOOT,
			BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_LOWER_LEG,
		),
		SkatingBodyParts(
			BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_FOOT,
			BodyPart.RIGHT_LOWER_LEG,
		),
	)

	// Centre of mass
	var comState: COMState? = null

	val pressure: BodyPartMap<Float> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()
	val releaseSmoothing: BodyPartMap<ReleaseSmoothing> = bodyPartMap()

	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeleton.effectiveSkatingCorrection) return

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
			fk[BodyPart.LEFT_FOOT]?.headPosition ?: return,
			fk[BodyPart.RIGHT_FOOT]?.headPosition ?: return,
			comState.position,
			comState.acceleration,
			floorLevel,
		)
		pressure[BodyPart.LEFT_FOOT] = leftPressure
		pressure[BodyPart.RIGHT_FOOT] = rightPressure

		val correctionStrength = skeletonConfig.ratios.skatingCorrectionStrength

		for ((bodyPart, mirrorBodyPart, ikTargetBodyPart) in skatingBodyParts) {
			val input = inputSkeleton[bodyPart] ?: return
			val bone = fk[bodyPart] ?: return
			val altBone = fk[mirrorBodyPart] ?: return
			val curPosition = bone.headPosition

			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked == true
			val altLocked = lockState[mirrorBodyPart]?.locked ?: false

			val (velocitySensitivity, accelerationSensitivity) = computeSensitivity(
				wasLocked,
				altLocked,
				bone.acceleration.len(),
				bone.velocity.linear,
				altBone.velocity.linear,
				// TODO Do something better
				pressure[bodyPart] ?: 0.1f,
			)

			// Consider locking BodyPart
			val isLocked = shouldLock(
				curPosition,
				if (wasLocked) {
					lastState.position
				} else {
					// The distance condition is disabled if not locked
					curPosition
				},
				bone.acceleration,
				if (input.isRotationActive) {
					bone.velocity.angular
				} else {
					Vector3.ZERO
				},
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

			val now = timeSource.markNow()
			val lastSmoothingOffset = releaseSmoothing[bodyPart]?.let { smoothing ->
				val multiplier = releaseSmoothingMultiplier(now, smoothing)
				if (multiplier > 0f) {
					smoothing.offset * multiplier
				} else {
					// Smoothing is finished, remove it from the map
					releaseSmoothing[bodyPart] = null
					null
				}
			}
			val activeSmoothingOffset = if (!isLocked && wasLocked) {
				// If unlocking, start smoothing from the locked position (plus last
				// smoothing offset if present) to the current position
				val lastLockPosition = lastSmoothingOffset?.let { offset ->
					lastState.position + offset
				} ?: lastState.position

				// Offset from the current position to the locked position
				val offset = lastLockPosition - curPosition

				// Save our new smoothing
				releaseSmoothing[bodyPart] = ReleaseSmoothing(
					now,
					releaseSmoothingDuration(offset.len()),
					offset,
				)
				// The first frame will always be the full offset, so just return that
				offset
			} else {
				lastSmoothingOffset
			}

			if (activeState.locked) {
				mutableIkTargets[ikTargetBodyPart] = activeSmoothingOffset?.let { offset ->
					activeState.position + offset
				} ?: activeState.position
			} else if (activeSmoothingOffset != null) {
				// Target position with smoothed offset if present
				mutableIkTargets[ikTargetBodyPart] = curPosition + activeSmoothingOffset
			}
		}
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			comState = null
			lockState.clear()
			releaseSmoothing.clear()
		}
	}
}
