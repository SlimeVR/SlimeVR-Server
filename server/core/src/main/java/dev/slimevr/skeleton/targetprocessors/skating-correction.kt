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
const val OFFSET_LIMIT_MAX = 0.2f

// Parameters to decide if a foot should be locked
// Higher makes it harder to unlock after being locked
const val LOCKED_UNLOCK_MULTIPLIER = 1.12f
const val FLOOR_DISTANCE_THRESHOLD = 0.065f
const val COM_DISTANCE_THRESHOLD = 0.3f
const val ACCELERATION_THRESHOLD_MIN = 0.4f
const val ACCELERATION_THRESHOLD_MAX = 1.3f

// Need to be locked for this long to actually be locked
const val LOCK_WARMUP_MIN = 0.250f
const val LOCK_WARMUP_MAX = 0.150f

// Smoothing
// The distance correction scales with the linear velocity and distance from locked position
const val DISTANCE_MIN = 0.01f
const val DISTANCE_MAX = 0.1f
const val DISTANCE_CORRECTION_MIN = 0.45f
const val DISTANCE_CORRECTION_MAX = 1f

// The correction speed accelerates with time
const val DURATION_CORRECTION_SPEED = 2f
const val DURATION_CORRECTION_WARMUP = 0.7f

// To not keep smoothing forever, making it so we'd run IK for nothing.
const val CORRECTION_EPSILON = 0.001f

data class LockState(
	val locked: Boolean,
	val shouldLockTime: ComparableTimeMark? = null,
	val position: Vector3? = null,
)

data class ReleaseSmoothing(
	val startTime: ComparableTimeMark,
	val offset: Vector3,
)

fun comLocked(
	wasLocked: Boolean,
	position: Vector3,
	centreOfMass: Vector3,
) = (position - centreOfMass).let { Vector3(it.x, 0f, it.z) }.len() <= COM_DISTANCE_THRESHOLD * (if (wasLocked) LOCKED_UNLOCK_MULTIPLIER else 1f)

fun floorLocked(
	wasLocked: Boolean,
	position: Vector3,
	floorLevel: Float,
) = position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD * (if (wasLocked) LOCKED_UNLOCK_MULTIPLIER else 1f)

fun accelLocked(
	wasLocked: Boolean,
	acceleration: Vector3,
	accelerationThreshold: Float,
) = acceleration.len() <= accelerationThreshold * (if (wasLocked) LOCKED_UNLOCK_MULTIPLIER else 1f)

fun computeLockState(
	lastState: LockState,
	now: ComparableTimeMark,
	lockWarmup: Float,
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

	if (shouldLock) {
		if (adjustedLastState.locked) return adjustedLastState
		if (adjustedLastState.shouldLockTime == null) return LockState(shouldLockTime = now, locked = false)
		return if ((now - adjustedLastState.shouldLockTime).inFloatingSeconds > lockWarmup) {
			LockState(true, position = position)
		} else {
			adjustedLastState
		}
	} else if (adjustedLastState.locked) {
		return LockState(locked = false)
	} else {
		return adjustedLastState
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
)

class SkatingCorrectionTargetProcessor(val settings: Settings, val skeleton: Skeleton) :
	SkeletonTargetProcessor,
	ResettableSkeletonProcessor {
	private val skatingBodyParts = arrayOf(
		SkatingBodyParts(
			BodyPart.LEFT_FOOT,
			BodyPart.LEFT_LOWER_LEG,
		),
		SkatingBodyParts(
			BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_LOWER_LEG,
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

		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds.coerceIn(0f, 1f)
		lastProcessTime = now

		// Centre of mass
		val com = centreOfMass(fk)
		// If no body part is locked due to com, then we can't rely on it.
		var noComLocked = true
		val comLock: BodyPartMap<Boolean> = bodyPartMap()
		for ((bodyPart, ikTargetBodyPart) in skatingBodyParts) {
			val isComLocked = comLocked(
				lockState[bodyPart]?.locked == true,
				mutableIkTargets[ikTargetBodyPart] ?: fk[ikTargetBodyPart]?.tailPosition ?: continue,
				com,
			)
			comLock[bodyPart] = isComLocked
			if (isComLocked) noComLocked = false
		}

		for ((bodyPart, ikTargetBodyPart) in skatingBodyParts) {
			val bone = fk[bodyPart] ?: continue
			val ikBone = fk[ikTargetBodyPart] ?: continue
			val curPosition = mutableIkTargets[ikTargetBodyPart] ?: ikBone.tailPosition
			val lastState = lockState[bodyPart] ?: LockState(locked = false)

			// Compute if the body part should be locked
			val shouldLock = (noComLocked || comLock[bodyPart] == true) &&
				floorLocked(lastState.locked, curPosition, floorLevel) &&
				accelLocked(lastState.locked, bone.acceleration, accelerationThreshold)

			// Update lock state
			val activeState = computeLockState(
				lastState,
				now,
				lockWarmup,
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
