package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark

// TODO We should probably just have globally available velocity on the whole skeleton,
//  we could do that as a processing step
data class VelocityState(
	val time: ComparableTimeMark,
	val position: Vector3,
	val rotation: Quaternion,
	val horizontalDistance: Float,
	val velocity: Vector3,
	val angularVelocityMagnitude: Float,
	val acceleration: Vector3,
)

data class COMState(
	val time: ComparableTimeMark,
	val position: Vector3,
	val velocity: Vector3,
	val acceleration: Vector3,
)

data class LockState(
	val locked: Boolean,
	val position: Vector3 = Vector3.NULL,
)

val VELOCITY_BODY_PARTS = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

const val FOOT_VELOCITY_SENSITIVITY = 1f
const val FOOT_ACCELERATION_SENSITIVITY = 1f

const val SKATING_LOCK_ENGAGE_PERCENT = 1.1f
const val SKATING_DISTANCE_THRESHOLD = 0.5f
const val SKATING_VELOCITY_THRESHOLD = 2.4f
const val SKATING_ROTATIONAL_VELOCITY_THRESHOLD = 4.5f
const val SKATING_ACCELERATION_THRESHOLD = 0.7f

const val FLOOR_CALIBRATION_OFFSET = 0.0025f
const val FLOOR_DISTANCE_THRESHOLD = 0.065f

// TODO Where are these numbers from?
val BODY_PART_MASSES = mapOf(
	BodyPart.HEAD to 0.0827f,
	BodyPart.LEFT_UPPER_ARM to 0.0263f,
	BodyPart.RIGHT_UPPER_ARM to 0.0263f,
	BodyPart.LEFT_LOWER_ARM to 0.0224f,
	BodyPart.RIGHT_LOWER_ARM to 0.0224f,
	BodyPart.CHEST to 0.1870f,
	BodyPart.WAIST to 0.1320f,
	BodyPart.HIP to 0.1530f,
	BodyPart.LEFT_UPPER_LEG to 0.1122f,
	BodyPart.RIGHT_UPPER_LEG to 0.1122f,
	BodyPart.LEFT_LOWER_LEG to 0.0620f,
	BodyPart.RIGHT_LOWER_LEG to 0.0620f,
)

// TODO Implement feet accel/velocity sensitivity calculation
fun shouldLock(
	velocity: VelocityState,
	thresholdMultiplier: Float,
	floorLevel: Float,
): Boolean = (velocity.horizontalDistance <= SKATING_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(velocity.velocity.len() <= SKATING_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(velocity.angularVelocityMagnitude <= SKATING_ROTATIONAL_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(velocity.position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(velocity.acceleration.len() <= SKATING_ACCELERATION_THRESHOLD * thresholdMultiplier)

// TODO Use this to calculate feet pressure
fun centerOfMass(
	bones: ComputedSkeleton,
): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.NULL) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCenter = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCenter * massEntry.value)
}

fun limitedDeltaTime(
	from: ComparableTimeMark,
	to: ComparableTimeMark,
	minimum: Float = 0.001f,
): Float = (to - from).inFloatingSeconds.coerceAtLeast(minimum)

fun computeComState(time: ComparableTimeMark, last: COMState?, com: Vector3): COMState = if (last != null) {
	val deltaTime = limitedDeltaTime(last.time, time)
	val comVelocity = (com - last.position) / deltaTime
	val comAcceleration = (comVelocity - last.velocity) / deltaTime
	COMState(
		time,
		com,
		comVelocity,
		comAcceleration,
	)
} else {
	COMState(
		time,
		com,
		Vector3.NULL,
		Vector3.NULL,
	)
}

fun computeVelocityState(
	time: ComparableTimeMark,
	last: VelocityState?,
	bone: BoneState,
): VelocityState = if (last != null) {
	val deltaPosition = bone.tailPosition - last.position
	val deltaTime = limitedDeltaTime(last.time, time)
	VelocityState(
		time,
		bone.tailPosition,
		bone.rotation,
		deltaPosition.len(),
		deltaPosition / deltaTime,
		// May need to be `angleToR` while polarity tracking is not implemented
		last.rotation.angleToQ(bone.rotation) / deltaTime,
		Vector3.NULL,
	)
} else {
	VelocityState(
		time,
		bone.tailPosition,
		bone.rotation,
		0f,
		Vector3.NULL,
		0f,
		Vector3.NULL,
	)
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
	// Last locked position could be retained if needed, but I can't think
	//  of a use
	LockState(
		false,
	)
} else {
	null
}

class SkatingCorrectionTargetProcessor(val settings: Settings) : SkeletonTargetProcessor {
	// Centre of mass
	var comState: COMState? = null

	// Do we need to store this or do we just want velocity?
	//  We can probably just pull the last state
	val velocity: BodyPartMap<VelocityState> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()

	override fun process(fk: ComputedSkeleton, ikTargets: IKTargets, floorLevel: Float): IKTargets {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.skatingCorrection) return ikTargets

		val curTime = timeSource.markNow()

		// Update centre of mass
		comState = computeComState(
			curTime,
			comState,
			centerOfMass(fk),
		)

		val correctionStrength = skeletonConfig.ratios.skatingCorrectionStrength

		for (bodyPart in VELOCITY_BODY_PARTS) {
			val curBone = fk[bodyPart] ?: continue

			// TODO Pull velocity out into the base skeleton, we need it elsewhere too
			val newVel = computeVelocityState(
				curTime,
				velocity[bodyPart],
				curBone,
			)
			velocity[bodyPart] = newVel

			// Consider locking BodyPart
			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked ?: false
			val isLocked = shouldLock(
				newVel,
				if (wasLocked) SKATING_LOCK_ENGAGE_PERCENT else 1f,
				floorLevel + FLOOR_CALIBRATION_OFFSET,
			)

			val activeState = computeLockState(
				wasLocked,
				isLocked,
				curBone.tailPosition,
			)?.also {
				// Track lock state changes
				lockState[bodyPart] = it
				// Otherwise pull the last state
			} ?: lastState ?: continue

			if (activeState.locked) {
				ikTargets[bodyPart] = activeState.position
			}
		}
		return ikTargets
	}
}
