package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

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

val VELOCITY_BODY_PARTS = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

val FOOT_VELOCITY_SENSITIVITY = 1f
val FOOT_ACCELERATION_SENSITIVITY = 1f

val SKATING_LOCK_ENGAGE_PERCENT = 1.1f
val SKATING_DISTANCE_THRESHOLD = 0.5f
val SKATING_VELOCITY_THRESHOLD = 2.4f
val SKATING_ROTATIONAL_VELOCITY_THRESHOLD = 4.5f
val SKATING_ACCELERATION_THRESHOLD = 0.7f

// TODO Floor level needs to be calibrated in some way
val FLOOR_LEVEL = 0f
val FLOOR_DISTANCE_THRESHOLD = 0.065f

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
): Boolean = (velocity.horizontalDistance <= SKATING_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(velocity.velocity.len() <= SKATING_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(velocity.angularVelocityMagnitude <= SKATING_ROTATIONAL_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(velocity.position.y - FLOOR_LEVEL <= FLOOR_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(velocity.acceleration.len() <= SKATING_ACCELERATION_THRESHOLD * thresholdMultiplier)

// TODO Use this to calculate feet pressure
fun centerOfMass(bones: Map<BodyPart, BoneState>): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.NULL) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCenter = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCenter * massEntry.value)
}

// Probably not a SkeletonProcessor, maybe computed processor or smth
class SkatingCorrectionProcessor : SkeletonProcessor {
	// Placeholder
	val curPositions: Map<BodyPart, BoneState> = emptyMap()

	// Do we need to store this or do we just want velocity? We can probably just pull
	//  the last state
	val velocity: MutableMap<BodyPart, VelocityState> = mutableMapOf()
	val lockStates: MutableMap<BodyPart, Boolean> = mutableMapOf()

	var comState: COMState? = null

	override fun process(state: SkeletonState): SkeletonState {
		val curTime = TimeSource.Monotonic.markNow()

		val lastComState = comState
		val com = centerOfMass(curPositions)
		val newComState = if (lastComState != null) {
			val deltaT = (curTime - lastComState.time).toDouble(DurationUnit.SECONDS).toFloat()
				.coerceAtLeast(0.001f)
			val comVelocity = (com - lastComState.position) / deltaT
			val comAcceleration = (comVelocity - lastComState.velocity) / deltaT
			COMState(
				curTime,
				com,
				comVelocity,
				comAcceleration,
			)
		} else {
			COMState(
				curTime,
				com,
				Vector3.NULL,
				Vector3.NULL,
			)
		}
		comState = newComState

		for (bodyPart in VELOCITY_BODY_PARTS) {
			val curBone = curPositions[bodyPart] ?: continue
			val lastVel = velocity[bodyPart]

			// Calculate velocity state
			val newVel = if (lastVel != null) {
				val deltaP = curBone.tailPosition - lastVel.position
				val deltaT =
					(curTime - lastVel.time).toDouble(DurationUnit.SECONDS).toFloat()
						.coerceAtLeast(0.001f)
				VelocityState(
					curTime,
					curBone.tailPosition,
					curBone.rotation,
					deltaP.len(),
					deltaP / deltaT,
					// May need to be `angleToR` while polarity tracking is not implemented
					lastVel.rotation.angleToQ(curBone.rotation) / deltaT,
					Vector3.NULL,
				)
			} else {
				VelocityState(
					curTime,
					curBone.tailPosition,
					curBone.rotation,
					0f,
					Vector3.NULL,
					0f,
					Vector3.NULL,
				)
			}
			velocity[bodyPart] = newVel

			// Consider locking BodyPart
			val wasLocked = lockStates.getOrDefault(bodyPart, false)
			val isLocked = shouldLock(
				newVel,
				if (wasLocked) SKATING_LOCK_ENGAGE_PERCENT else 1f,
			)
			lockStates[bodyPart] = isLocked
		}
		return state
	}
}

class FloorClipProcessor : SkeletonProcessor {
	fun getDisplacement(footPos: Vector3): Vector3 = Vector3(0f, footPos.y.coerceAtMost(0f), 0f)

	override fun process(state: SkeletonState): SkeletonState {
		val newBones = state.rawBones.mapValues { (bodyPart, bone) -> }
		state.rawBones[BodyPart.LEFT_FOOT]?.let {
			val correction = getDisplacement(it.offset)
			// TODO Apply displacement onto skeleton
		}
		return state
	}
}
