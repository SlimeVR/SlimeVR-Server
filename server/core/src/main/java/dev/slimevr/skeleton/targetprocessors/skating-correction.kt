package dev.slimevr.skeleton.targetprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.collections.set
import kotlin.time.ComparableTimeMark

// Max correction offset length, drags position closer
const val OFFSET_LIMIT_MIN = 0.12f
const val OFFSET_LIMIT_MAX = 0.20f

// Multiply the acceleration threshold
const val COM_DISTANCE_MULTIPLIER = 1.5f
const val LIFT_MIN = -0.85f
const val LIFT_MAX = 0.4f

// Parameters to decide if a foot should be locked
// Higher makes it harder to unlock after being locked
const val LOCKED_UNLOCK_MULTIPLIER = 1.1f
const val FLOOR_DISTANCE_THRESHOLD = 0.065f
const val ACCELERATION_THRESHOLD_MIN = 0.5f
const val ACCELERATION_THRESHOLD_MAX = 1.5f

// Need to be locked for this long to actually be locked
const val LOCK_WARMUP_MIN = 0.150f
const val LOCK_WARMUP_MAX = 0.080f

// Need to be unlocked for this long to actually be unlocked
const val UNLOCK_WARMUP_MIN = 0.030f
const val UNLOCK_WARMUP_MAX = 0.050f

// Smoothing
// The distance correction scales with the linear velocity and distance from locked position
const val DISTANCE_MIN = 0.01f
const val DISTANCE_MAX = 0.1f
const val DISTANCE_CORRECTION_MIN = 0f
const val DISTANCE_CORRECTION_MAX = 1f

// The correction speed accelerates with time
const val DURATION_CORRECTION_SPEED = 2f
const val DURATION_CORRECTION_WARMUP = 0.7f

// To not keep smoothing forever, making it so we'd run IK for nothing.
const val CORRECTION_EPSILON = 0.001f

data class LockState(
	val locked: Boolean,
	val lockChangeTime: ComparableTimeMark? = null,
	val unlockChangeTime: ComparableTimeMark? = null,
	val position: Vector3? = null,
)

data class ReleaseSmoothing(
	val startTime: ComparableTimeMark,
	val offset: Vector3,
)

/**
 * Horizontal distance from the centre of mass, scaled by COM_DISTANCE_MULTIPLIER
 */
fun computeComDistance(
	position: Vector3,
	centreOfMass: Vector3,
) = (position - centreOfMass).let { Vector3(it.x, 0f, it.z) }.len() * COM_DISTANCE_MULTIPLIER

/**
 * Lower comDistance = higher multiplier
 * Higher comDistance = lower multiplier
 * Lower comDistancesAverage = lower multiplier
 * Higher comDistancesAverage = higher multiplier
 */
fun computeComMultiplier(
	comDistance: Float,
	comDistancesAverage: Float,
) = (1f + comDistancesAverage) * (comDistancesAverage / comDistance)

/**
 * Remaps the localLiftPosition (thigh local position).
 * localLiftPosition.unit().y goes from -1 (downwards) to +1 (upwards)
 */
fun computeLiftMultiplier(
	localLiftPosition: Vector3,
) = FastMath.remapClamped(localLiftPosition.unit().y, LIFT_MIN, LIFT_MAX, 1f, 0f)

/**
 * Locked if close enough to the floor
 */
fun floorLocked(
	wasLocked: Boolean,
	position: Vector3,
	floorLevel: Float,
) = position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD * (if (wasLocked) LOCKED_UNLOCK_MULTIPLIER else 1f)

/**
 * Locked if accel is below the threshold
 */
fun accelLocked(
	wasLocked: Boolean,
	acceleration: Vector3,
	accelerationThreshold: Float,
) = acceleration.len() <= accelerationThreshold * (if (wasLocked) LOCKED_UNLOCK_MULTIPLIER else 1f)

fun updateLockState(
	lastState: LockState,
	now: ComparableTimeMark,
	lockWarmup: Float,
	unlockWarmup: Float,
	shouldLock: Boolean,
	position: Vector3,
	offsetLimit: Float,
): LockState {
	val adjustedLastState = if (lastState.position != null) {
		// Keep locked position within a limit
		val unlockedVerticalPosition = Vector3(lastState.position.x, position.y, lastState.position.z)
		val positionDelta = position - unlockedVerticalPosition
		val limitRatio = (1 - (offsetLimit / positionDelta.len())).coerceAtLeast(0f)
		lastState.copy(position = unlockedVerticalPosition.lerp(position, limitRatio))
	} else {
		lastState
	}

	// No lock change
	if (shouldLock == adjustedLastState.locked) {
		return adjustedLastState
	}

	// Need to have been locked or unlocked for enough time
	return if (shouldLock) {
		// Should lock
		if (adjustedLastState.lockChangeTime == null) {
			adjustedLastState.copy(lockChangeTime = now, unlockChangeTime = null)
		} else if ((now - adjustedLastState.lockChangeTime).inFloatingSeconds > lockWarmup) {
			// Locked state needs position when lock happened
			LockState(locked = true, position = position)
		} else {
			adjustedLastState
		}
	} else {
		// Should unlock
		if (adjustedLastState.unlockChangeTime == null) {
			adjustedLastState.copy(lockChangeTime = null, unlockChangeTime = now)
		} else if ((now - adjustedLastState.unlockChangeTime).inFloatingSeconds > unlockWarmup) {
			// Unlocked state doesn't need position at unlock; we just use the current position.
			LockState(locked = false)
		} else {
			adjustedLastState
		}
	}
}

fun computeReleaseSmoothing(offset: Vector3, horizontalVelocity: Float, secondsUnlocked: Float, deltaTime: Float): Vector3 {
	val length = offset.len()
	if (length <= CORRECTION_EPSILON) return Vector3.ZERO

	val distanceMultiplier = FastMath.remapClamped(length, DISTANCE_MIN, DISTANCE_MAX, DISTANCE_CORRECTION_MIN, DISTANCE_CORRECTION_MAX)
	val velocityCorrection = horizontalVelocity * distanceMultiplier
	val durationCorrection = DURATION_CORRECTION_SPEED * (secondsUnlocked / DURATION_CORRECTION_WARMUP).coerceAtMost(1f) * distanceMultiplier
	val totalCorrection = (velocityCorrection + durationCorrection) * deltaTime
	val remaining = (length - totalCorrection).coerceAtLeast(0f)
	return offset * (remaining / length)
}

private data class SkatingBodyParts(
	// ex. Left foot
	val bodyPart: BodyPart,
	// ex. Left lower leg (ankle)
	val ikTargetBodyPart: BodyPart,
	// ex. Left upper leg (thigh)
	val liftBodyPart: BodyPart,
)

class SkatingCorrectionTargetProcessor(val settings: Settings, val skeleton: Skeleton) :
	SkeletonTargetProcessor,
	ResettableSkeletonProcessor {
	private val skatingBodyParts = arrayOf(
		SkatingBodyParts(
			BodyPart.LEFT_FOOT,
			BodyPart.LEFT_LOWER_LEG,
			BodyPart.LEFT_UPPER_LEG,
		),
		SkatingBodyParts(
			BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_LOWER_LEG,
			BodyPart.RIGHT_UPPER_LEG,
		),
	)

	var lastProcessTime = timeSource.markNow()
	val lockState: BodyPartMap<LockState> = bodyPartMap()
	val releaseSmoothing: BodyPartMap<ReleaseSmoothing> = bodyPartMap()

	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeleton.effectiveSkatingCorrection) return
		val offsetLimit = FastMath.remapClamped(skeletonConfig.ratios.skatingCorrectionStrength, 0f, 1f, OFFSET_LIMIT_MIN, OFFSET_LIMIT_MAX)
		val accelerationThreshold = FastMath.remapClamped(skeletonConfig.ratios.skatingCorrectionStrength, 0f, 1f, ACCELERATION_THRESHOLD_MIN, ACCELERATION_THRESHOLD_MAX)
		val lockWarmup = FastMath.remapClamped(skeletonConfig.ratios.skatingCorrectionStrength, 0f, 1f, LOCK_WARMUP_MIN, LOCK_WARMUP_MAX)
		val unlockWarmup = FastMath.remapClamped(skeletonConfig.ratios.skatingCorrectionStrength, 0f, 1f, UNLOCK_WARMUP_MIN, UNLOCK_WARMUP_MAX)

		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds.coerceIn(0f, 1f)
		lastProcessTime = now

		// Compute the distance from the centre of mass for each foot
		val com = centreOfMass(fk)
		val comDistances: BodyPartMap<Float> = bodyPartMap()
		for ((bodyPart, ikTargetBodyPart) in skatingBodyParts) {
			val position = mutableIkTargets[ikTargetBodyPart] ?: fk[ikTargetBodyPart]?.tailPosition ?: continue
			comDistances[bodyPart] = computeComDistance(position, com)
		}
		val comDistancesAverage = comDistances.values.average().toFloat()

		for ((bodyPart, ikTargetBodyPart, liftBodyPart) in skatingBodyParts) {
			val bone = fk[bodyPart] ?: continue
			val ikBone = fk[ikTargetBodyPart] ?: continue
			val liftBone = fk[liftBodyPart] ?: continue
			val curPosition = mutableIkTargets[ikTargetBodyPart] ?: ikBone.tailPosition
			val lastState = lockState[bodyPart] ?: LockState(locked = false)

			// comMultiplier increases the threshold when the foot is alone near COM, or together far from COM
			val comMultiplier = computeComMultiplier(comDistances[bodyPart] ?: continue, comDistancesAverage)
			// liftMultiplier decreases the threshold when the upper leg is lifted. Useful when sitting/lying down
			val liftMultiplier = computeLiftMultiplier(liftBone.localTailPosition)
			// Apply multipliers to the acceleration threshold
			val scaledAccelerationThreshold = accelerationThreshold * comMultiplier * liftMultiplier

			// Compute if the body part should be locked
			val floorLocked = floorLocked(lastState.locked, curPosition, floorLevel)
			val accelLocked = accelLocked(lastState.locked, bone.acceleration, scaledAccelerationThreshold)
			val shouldLock = floorLocked && accelLocked

			// Update lock state
			val activeState = updateLockState(
				lastState,
				now,
				lockWarmup,
				unlockWarmup,
				shouldLock,
				curPosition,
				offsetLimit,
			)
			lockState[bodyPart] = activeState

			// Continue smoothing if we were
			val smoothingOffset = releaseSmoothing[bodyPart]?.let { lastSmoothing ->
				// Get new smoothing
				val newSmoothing = computeReleaseSmoothing(
					lastSmoothing.offset,
					bone.velocity.linear.let { Vector3(it.x, 0f, it.z) }.len(),
					(now - lastSmoothing.startTime).inFloatingSeconds,
					deltaTime,
				)

				// Update the last smoothing to this new smoothing, if any within epsilon.
				if (newSmoothing.len() > CORRECTION_EPSILON) {
					releaseSmoothing[bodyPart] = lastSmoothing.copy(offset = newSmoothing)
					newSmoothing
				} else {
					// Smoothing finished
					releaseSmoothing[bodyPart] = null
					null
				}
			}
			// Start smoothing fresh if we just unlocked
			val activeSmoothingOffset = if (!activeState.locked && lastState.locked && lastState.position != null) {
				// If unlocking, start smoothing from the locked position (plus last
				// smoothing offset if present) to the current position
				val lastLockPosition = smoothingOffset?.let { offset ->
					lastState.position + offset
				} ?: lastState.position

				// Offset from the current position to the locked position
				(lastLockPosition - curPosition).let { Vector3(it.x, 0f, it.z) }.also {
					// Save our new smoothing
					releaseSmoothing[bodyPart] = ReleaseSmoothing(
						now,
						it,
					)
				}
			} else {
				// Just use smoothing we already computed above
				smoothingOffset
			}

			if (activeState.locked && activeState.position != null) {
				mutableIkTargets[ikTargetBodyPart] = if (activeSmoothingOffset != null) {
					activeState.position + activeSmoothingOffset
				} else {
					activeState.position
				}
			} else if (activeSmoothingOffset != null) {
				// Target position with smoothed offset if present
				mutableIkTargets[ikTargetBodyPart] = curPosition + activeSmoothingOffset
			}
		}
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			lastProcessTime = timeSource.markNow()
			lockState.clear()
			releaseSmoothing.clear()
		}
	}
}
