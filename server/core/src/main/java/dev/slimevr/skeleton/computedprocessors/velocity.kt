package dev.slimevr.skeleton.computedprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.SkeletonComputedProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.ZERO_VELOCITY
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.Duration.Companion.milliseconds

private data class VelocityBoneData(
	val rotation: Quaternion,
	val position: Vector3,
)

private fun computeVelocity(currentVelocityData: VelocityBoneData, lastVelocityData: VelocityBoneData, deltaTime: Float): Velocity {
	val deltaPosition = currentVelocityData.position - lastVelocityData.position
	val deltaRotation = currentVelocityData.rotation / lastVelocityData.rotation
	return Velocity(
		linear = deltaPosition / deltaTime,
		angular = currentVelocityData.rotation.sandwich(deltaRotation.toRotationVector()) / deltaTime,
	)
}

// We smooth out the velocity since if a tracker is sending at 100tps and skeleton is at 500hz,
//  4 frames out of 5 will have little to no velocity, so we need to smooth at least across those frames.
private val SMOOTHING_WINDOW = 40.milliseconds.inFloatingSeconds
private fun smoothVelocity(currentVelocity: Velocity, lastVelocity: Velocity, deltaTime: Float): Velocity {
	val t = (deltaTime / SMOOTHING_WINDOW).coerceAtMost(1f)
	return Velocity(
		linear = lastVelocity.linear.lerp(currentVelocity.linear, t),
		angular = lastVelocity.angular.lerp(currentVelocity.angular, t),
	)
}

/**
 * Computes linear (m/s) and angular (rad/s) velocity for the bones.
 */
class VelocityComputedProcessor : SkeletonComputedProcessor {
	private val lastVelocities: BodyPartMap<Velocity> = bodyPartMap()
	private val lastVelocityBoneData: BodyPartMap<VelocityBoneData> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableComputedSkeleton: ComputedSkeleton) {
		// One clock read for the whole pass, so every bone shares the same interval
		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds
		lastProcessTime = now

		mutableComputedSkeleton.forEachBone { part, bone ->
			val lastVelocityData = lastVelocityBoneData[part]
			val lastVelocity = lastVelocities[part] ?: ZERO_VELOCITY

			// Compute current velocity
			val currentVelocityData = VelocityBoneData(bone.rotation, bone.tailPosition)
			val currentVelocity = lastVelocityData?.let { computeVelocity(currentVelocityData, it, deltaTime) } ?: ZERO_VELOCITY

			// Smooth velocity before setting it
			val newVelocity = smoothVelocity(currentVelocity, lastVelocity, deltaTime)
			mutableComputedSkeleton[part] = bone.copy(velocity = newVelocity)

			lastVelocityBoneData[part] = currentVelocityData
			lastVelocities[part] = newVelocity
		}
	}
}
