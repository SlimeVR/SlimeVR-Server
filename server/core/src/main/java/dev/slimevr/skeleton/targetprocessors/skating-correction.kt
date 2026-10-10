package dev.slimevr.skeleton.targetprocessors

import com.jme3.math.FastMath
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

// Higher makes it harder to unlock after being locked
const val SKATING_LOCK_DISENGAGE_PERCENT = 1.1f

// Minimum strength for the lock thresholds
const val CORRECTION_STRENGTH_MIN = 0.35f

// Main parameters to decide if a foot is locked or not
const val SKATING_DISTANCE_THRESHOLD = 0.25f
const val FLOOR_DISTANCE_THRESHOLD = 0.08f
const val SKATING_ANGULAR_VELOCITY_THRESHOLD = 5.8f
const val SKATING_ACCELERATION_THRESHOLD = 1f

const val PARAM_SCALAR_MAX = 3.2f
const val PARAM_SCALAR_MID = 1.0f
const val PARAM_SCALAR_MIN = 0.25f

// The point at which the scalar is at the max or min depending on accel
const val MAX_SCALAR_ACCEL = 0.2f
const val MIN_SCALAR_ACCEL = 0.9f

// The point at which the scalar is at it max or min in a double locked foot situation
const val MAX_SCALAR_DORMANT = 0.2f
const val MIN_SCALAR_DORMANT = 1.50f

// The point at which the scalar is at its max or min in a single locked foot situation
const val MAX_SCALAR_ACTIVE = 0.1f
const val MIN_SCALAR_ACTIVE = 1.75f

// Maximum scalars for the pressure on each foot
const val PRESSURE_SCALAR_MIN = 0.1f
const val PRESSURE_SCALAR_MAX = 1.9f

// The distance correction scales with the distance from locked position to curPosition
const val DISTANCE_MIN = 0.01f
const val DISTANCE_MAX = 0.05f
const val DISTANCE_CORRECTION_MIN = 0.55f
const val DISTANCE_CORRECTION_MAX = 0.70f

// The correction speed accelerates with time
const val DURATION_CORRECTION_SPEED = 0.5f
const val DURATION_CORRECTION_WARMUP = 1.75f

// To not keep smoothing forever, making it so we'd run IK for nothing.
const val CORRECTION_EPSILON = 0.001f

data class LockState(
	val locked: Boolean,
	val position: Vector3 = Vector3.ZERO,
)

data class ReleaseSmoothing(
	val startTime: ComparableTimeMark,
	val offset: Vector3,
)

/**
 * Returns if a foot is considered as planted and should be locked using:
 * - position delta from the locked position
 * - vertical translation from floor
 * - angular velocity magnitude
 * - acceleration magnitude
 */
fun shouldLock(
	position: Vector3,
	lockedPosition: Vector3?,
	acceleration: Vector3,
	angularVelocity: Vector3?,
	floorLevel: Float = 0f,
	correctionStrength: Float = 1f,
	velocitySensitivity: Float = 1f,
	accelerationSensitivity: Float = 1f,
): Boolean {
	val thresholdMultiplier = (if (lockedPosition != null) SKATING_LOCK_DISENGAGE_PERCENT else 1f) * correctionStrength
	val lockedToCorrectedPosition = lockedPosition == null || (position - lockedPosition).let { Vector3(it.x, 0f, it.z) }.len() <= SKATING_DISTANCE_THRESHOLD * thresholdMultiplier
	val lockedToFloor = position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD * thresholdMultiplier
	val lockedAngular = angularVelocity == null || angularVelocity.len() <= SKATING_ANGULAR_VELOCITY_THRESHOLD * thresholdMultiplier * velocitySensitivity
	val lockedAccel = acceleration.len() <= SKATING_ACCELERATION_THRESHOLD * thresholdMultiplier * accelerationSensitivity
	return lockedToCorrectedPosition && lockedToFloor && lockedAngular && lockedAccel
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
	val footScalarVel: Float = getFootLockLikelihood(
		velocity,
		altVelocity,
		isLocked && altIsLocked,
	)

	// Combine the scalars to get the final scalars
	val footSensitivityVel = (
		((footScalarAccel + footScalarVel) / 2f) *
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
fun getFootScalarAccel(isLocked: Boolean, accelerationMagnitude: Float): Float = if (isLocked) {
	FastMath.clampedRemap(accelerationMagnitude, MAX_SCALAR_ACCEL, MIN_SCALAR_ACCEL, PARAM_SCALAR_MAX, PARAM_SCALAR_MIN)
} else {
	PARAM_SCALAR_MID
}

// Calculate a scalar using the velocity of the foot trackers and the lock states to
//  calculate a scalar to apply to the non acceleration based hyperparameters when
//  calculating lock states
fun getFootLockLikelihood(primaryFootVel: Vector3, otherFootVel: Vector3, bothLocked: Boolean): Float = if (bothLocked) {
	val velocityDiff = (primaryFootVel - otherFootVel).let { Vector3(it.x, 0f, it.z) }.len()
	FastMath.clampedRemap(velocityDiff, MAX_SCALAR_DORMANT, MIN_SCALAR_DORMANT, PARAM_SCALAR_MAX, PARAM_SCALAR_MIN)
} else {
	val velocityDiffAbs = abs(primaryFootVel.len() - otherFootVel.len())
	FastMath.clampedRemap(velocityDiffAbs, MAX_SCALAR_ACTIVE, MIN_SCALAR_ACTIVE, PARAM_SCALAR_MAX, PARAM_SCALAR_MIN)
}

fun computeReleaseSmoothing(offset: Vector3, horizontalSpeed: Float, secondsUnlocked: Float, deltaTime: Float): Vector3 {
	val length = offset.len()
	if (length <= CORRECTION_EPSILON) return Vector3.ZERO

	val distanceCorrection = horizontalSpeed * FastMath.clampedRemap(length, DISTANCE_MIN, DISTANCE_MAX, DISTANCE_CORRECTION_MIN, DISTANCE_CORRECTION_MAX)
	val durationCorrection = DURATION_CORRECTION_SPEED * (secondsUnlocked / DURATION_CORRECTION_WARMUP).coerceAtMost(1f)
	val totalCorrection = (distanceCorrection + durationCorrection) * deltaTime
	val remaining = (length - totalCorrection).coerceAtLeast(0f)
	return offset * (remaining / length)
}

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

	var lastProcessTime = timeSource.markNow()
	var comState: COMState? = null
	val pressure: BodyPartMap<Float> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()
	val releaseSmoothing: BodyPartMap<ReleaseSmoothing> = bodyPartMap()

	override fun process(mutableIkTargets: IKTargets, inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeleton.effectiveSkatingCorrection) return

		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds.coerceIn(0f, 1f)
		lastProcessTime = now

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

		val correctionStrength = FastMath.remap(skeletonConfig.ratios.skatingCorrectionStrength, 0f, 1f, CORRECTION_STRENGTH_MIN, 1f)

		for ((bodyPart, mirrorBodyPart, ikTargetBodyPart) in skatingBodyParts) {
			val input = inputSkeleton[bodyPart] ?: continue
			val bone = fk[bodyPart] ?: continue
			val altBone = fk[mirrorBodyPart] ?: continue
			val ikBone = fk[ikTargetBodyPart] ?: continue
			val curPosition = mutableIkTargets[ikTargetBodyPart] ?: ikBone.tailPosition

			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked == true
			val altLocked = lockState[mirrorBodyPart]?.locked ?: false

			val (velocitySensitivity, accelerationSensitivity) = computeSensitivity(
				wasLocked,
				altLocked,
				bone.acceleration.len(),
				bone.velocity.linear,
				altBone.velocity.linear,
				pressure[bodyPart] ?: 0.5f,
			)

			// Consider locking BodyPart
			val isLocked = shouldLock(
				curPosition,
				if (wasLocked) {
					lastState.position
				} else {
					null
				},
				bone.acceleration,
				if (input.isRotationActive) {
					bone.velocity.angular
				} else {
					null
				},
				floorLevel,
				correctionStrength,
				velocitySensitivity,
				accelerationSensitivity,
			)

			// Update lock state
			val activeState = computeLockState(
				wasLocked,
				isLocked,
				curPosition,
			)?.also {
				// Track lock state changes
				lockState[bodyPart] = it
				// Otherwise pull the last state
			} ?: lastState ?: continue

			// Continue smoothing
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
			// Start smoothing
			val activeSmoothingOffset = if (!isLocked && wasLocked) {
				// If unlocking, start smoothing from the locked position (plus last
				// smoothing offset if present) to the current position
				val lastLockPosition = smoothingOffset?.let { offset ->
					lastState.position + offset
				} ?: lastState.position

				// Offset from the current position to the locked position
				val offset = lastLockPosition - curPosition

				// Save our new smoothing
				releaseSmoothing[bodyPart] = ReleaseSmoothing(
					now,
					offset,
				)
				// The first frame will always be the full offset, so just return that
				offset
			} else {
				smoothingOffset
			}

			if (activeState.locked) {
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
			comState = null
			pressure.clear()
			lockState.clear()
			releaseSmoothing.clear()
		}
	}
}
