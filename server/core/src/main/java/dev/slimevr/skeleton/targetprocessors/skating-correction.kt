package dev.slimevr.skeleton.targetprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark

data class COMState(
	val time: ComparableTimeMark,
	val position: Vector3,
	val velocity: Vector3,
	val acceleration: Vector3,
)

data class LockState(
	val locked: Boolean,
	val position: Vector3 = Vector3.ZERO,
)

val VELOCITY_BODY_PARTS = arrayOf(BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG)

const val FOOT_VELOCITY_SENSITIVITY = 1f
const val FOOT_ACCELERATION_SENSITIVITY = 1f

const val SKATING_LOCK_ENGAGE_PERCENT = 1.1f

// All squared for performance
const val SKATING_DISTANCE_THRESHOLD = 0.25f
const val SKATING_LINEAR_VELOCITY_THRESHOLD = 5.76f
const val SKATING_ANGULAR_VELOCITY_THRESHOLD = 20.25f
const val SKATING_ACCELERATION_THRESHOLD = 0.49f

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
// TODO check if the first line (with SKATING_DISTANCE_THRESHOLD) is correct
fun shouldLock(
	position: Vector3,
	lastPosition: Vector3,
	acceleration: Vector3,
	velocity: Velocity,
	thresholdMultiplier: Float,
	floorLevel: Float,
): Boolean = ((position - lastPosition).let { Vector3(it.x, 0f, it.z) }.lenSq() <= SKATING_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(velocity.linear.lenSq() <= SKATING_LINEAR_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(velocity.angular.lenSq() <= SKATING_ANGULAR_VELOCITY_THRESHOLD * thresholdMultiplier) &&
	(position.y - floorLevel <= FLOOR_DISTANCE_THRESHOLD * thresholdMultiplier) &&
	(acceleration.lenSq() <= SKATING_ACCELERATION_THRESHOLD * thresholdMultiplier)

// TODO Use this to calculate feet pressure
fun centerOfMass(
	bones: ComputedSkeleton,
): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.ZERO) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCenter = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCenter * massEntry.value)
}

fun computeComState(time: ComparableTimeMark, last: COMState?, com: Vector3): COMState = if (last != null) {
	val deltaTime = (time - last.time).inFloatingSeconds
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
		Vector3.ZERO,
		Vector3.ZERO,
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

	val lastLockedPositions: BodyPartMap<Vector3> = bodyPartMap()
	val lockState: BodyPartMap<LockState> = bodyPartMap()

	override fun process(mutableIkTargets: IKTargets, fk: ComputedSkeleton, floorLevel: Float) {
		val skeletonConfig = settings.context.state.value.data.skeletonConfig
		if (!skeletonConfig.toggles.skatingCorrection) return

		val curTime = timeSource.markNow()

		// Update centre of mass
		comState = computeComState(
			curTime,
			comState,
			centerOfMass(fk),
		)

		val correctionStrength = skeletonConfig.ratios.skatingCorrectionStrength // TODO

		for (bodyPart in VELOCITY_BODY_PARTS) {
			val curBone = fk[bodyPart] ?: continue
			val curPosition = curBone.tailPosition

			// Consider locking BodyPart
			val lastState = lockState[bodyPart]
			val wasLocked = lastState?.locked == true
			val isLocked = shouldLock(
				curPosition,
				lastLockedPositions[bodyPart] ?: curPosition,
				curBone.acceleration,
				curBone.velocity,
				if (wasLocked) SKATING_LOCK_ENGAGE_PERCENT else 1f,
				floorLevel + FLOOR_CALIBRATION_OFFSET,
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
				lastLockedPositions[bodyPart] = activeState.position
			}
		}
	}
}
