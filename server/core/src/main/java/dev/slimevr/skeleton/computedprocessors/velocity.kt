package dev.slimevr.skeleton.computedprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonComputedProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.ZERO_VELOCITY
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.millisecondsInSecond
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.ResetType

private data class BoneSample(
	val rotation: Quaternion,
	val position: Vector3,
)

private data class VelocitySample(
	val boneSample: BoneSample,
	val velocity: Velocity,
)

private fun computeVelocity(currentBoneSample: BoneSample, lastBoneSample: BoneSample, deltaTime: Float): Velocity {
	val deltaPosition = currentBoneSample.position - lastBoneSample.position
	val deltaRotation = currentBoneSample.rotation / lastBoneSample.rotation
	return Velocity(
		linear = deltaPosition / deltaTime,
		angular = currentBoneSample.rotation.sandwich(deltaRotation.toRotationVectorQ()) / deltaTime,
	)
}

private const val SMOOTHING_MS = 20f
private fun smoothVelocity(currentVelocity: Velocity, lastVelocity: Velocity, deltaTime: Float): Velocity {
	val t = (deltaTime * (millisecondsInSecond / SMOOTHING_MS)).coerceAtMost(1f)
	return Velocity(
		linear = lastVelocity.linear.lerp(currentVelocity.linear, t),
		angular = lastVelocity.angular.lerp(currentVelocity.angular, t),
	)
}

/**
 * Computes linear (m/s) and angular (rad/s) velocity for the bones.
 */
class VelocityComputedProcessor :
	SkeletonComputedProcessor,
	ResettableSkeletonProcessor {
	private val lastVelocities: BodyPartMap<VelocitySample> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableComputedSkeleton: ComputedSkeleton, inputSkeleton: InputSkeleton) {
		// One clock read for the whole pass, so every bone shares the same interval
		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds
		lastProcessTime = now

		for ((bodyPart, bone) in mutableComputedSkeleton) {
			val lastVelocitySample = lastVelocities[bodyPart]

			// Compute velocity
			val currentBoneSample = BoneSample(bone.rotation, bone.tailPosition)
			val smoothedVelocity = if (lastVelocitySample != null) {
				val currentVelocity = computeVelocity(currentBoneSample, lastVelocitySample.boneSample, deltaTime)
				smoothVelocity(currentVelocity, lastVelocitySample.velocity, deltaTime)
			} else {
				ZERO_VELOCITY
			}

			mutableComputedSkeleton[bodyPart] = bone.copy(velocity = smoothedVelocity)
			lastVelocities[bodyPart] = VelocitySample(currentBoneSample, smoothedVelocity)
		}
	}

	override fun reset(resetType: ResetType) {
		lastVelocities.clear()
		lastProcessTime = timeSource.markNow()
	}
}
