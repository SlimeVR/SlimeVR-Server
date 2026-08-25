package dev.slimevr.skeleton

import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark
import kotlin.time.DurationUnit

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

val FOOT_VELOCITY_SENSITIVITY = 1f
val FOOT_ACCELERATION_SENSITIVITY = 1f

val SKATING_LOCK_ENGAGE_PERCENT = 1.1f
val SKATING_DISTANCE_THRESHOLD = 0.5f
val SKATING_VELOCITY_THRESHOLD = 2.4f
val SKATING_ROTATIONAL_VELOCITY_THRESHOLD = 4.5f
val SKATING_ACCELERATION_THRESHOLD = 0.7f

// TODO Floor level needs to be calibrated in some way; originally done on full reset
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
fun centerOfMass(bones: ComputedSkeleton): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.NULL) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCenter = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCenter * massEntry.value)
}

// Probably not a SkeletonProcessor, maybe computed processor or smth
class SkatingCorrectionProcessor : SkeletonTargetProcessor {
	override var enabled: Boolean = true

	// Center of mass
	var comState: COMState? = null

	// Do we need to store this or do we just want velocity? We can probably just pull
	//  the last state
	val velocity: BodyPartMap<VelocityState> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()

	override fun process(fk: ComputedSkeleton, ikTargets: IKTargets): IKTargets {
		val curTime = timeSource.markNow()

		// Update center of mass
		val lastComState = comState
		val com = centerOfMass(fk)
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
			val curBone = fk[bodyPart] ?: continue

			// TODO Pull velocity out into the base skeleton, we need it elsewhere too
			// Calculate velocity state
			val newVel = velocity[bodyPart]?.let { lastVel ->
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
			} ?: VelocityState(
				curTime,
				curBone.tailPosition,
				curBone.rotation,
				0f,
				Vector3.NULL,
				0f,
				Vector3.NULL,
			)
			velocity[bodyPart] = newVel

			// Consider locking BodyPart
			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked ?: false
			val isLocked = shouldLock(
				newVel,
				if (wasLocked) SKATING_LOCK_ENGAGE_PERCENT else 1f,
			)

			// Toggle the locked state if changing lock state
			if (isLocked) {
				val lockState = if (!wasLocked) {
					LockState(
						true,
						curBone.tailPosition,
					).also {
						lockState[bodyPart] = it
					}
				} else {
					lastState
				}
				ikTargets[bodyPart] = lockState.position
			} else if (wasLocked) {
				// Last locked position could be retained if needed, but I can't think
				//  of a use
				lockState[bodyPart] = LockState(
					false,
				)
			}
		}
		return ikTargets
	}
}

class FloorClipProcessor(
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG),
) : SkeletonTargetProcessor {
	override var enabled: Boolean = true

	override fun process(fk: ComputedSkeleton, ikTargets: IKTargets): IKTargets {
		for (bodyPart in bodyParts) {
			// Get existing target or make a new one at the current bone position
			val target = ikTargets[bodyPart] ?: fk[bodyPart]?.tailPosition ?: continue
			// Snap the target up to the floor if it's under
			ikTargets[bodyPart] = Vector3(target.x, target.y.coerceAtLeast(0f), target.z)
		}
		return ikTargets
	}
}
