package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.mutateCopy
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import kotlin.time.ComparableTimeMark

fun computeVelocity(
	currentBone: BoneState,
	lastBone: BoneState,
	lastTime: ComparableTimeMark,
): Velocity {
	val deltaPosition = currentBone.tailPosition - lastBone.tailPosition
	val deltaRotation = currentBone.rotation / lastBone.rotation
	val deltaTime = (timeSource.markNow() - lastTime).inFloatingSeconds
	return Velocity(
		linear = deltaPosition / deltaTime,
		angular = deltaRotation.toRotationVector() / deltaTime, // TODO math
	)
}

/**
 * Computes linear (m/s) and angular (rad/s) velocity for the bones.
 */
class VelocityFkProcessor : SkeletonFkProcessor {
	private var lastProcessTime = timeSource.markNow()

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		fk.forEachBone { part, bone ->
			val velocity = computeVelocity(bone, bone, lastProcessTime)
			inputSkeleton[part] = inputSkeleton[part]?.copy(velocity = velocity)
		}
		lastProcessTime = timeSource.markNow()
	}
}
