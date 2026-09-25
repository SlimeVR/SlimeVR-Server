package dev.slimevr.skeleton.computedprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonComputedProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.ZERO_VELOCITY
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.findFirstParent
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.ResetType

private data class VelocityData(
	val rotation: Quaternion,
	val position: Vector3,
)

private fun computeVelocity(currentVelocityData: VelocityData, lastVelocityData: VelocityData, deltaTime: Float): Velocity {
	val deltaPosition = currentVelocityData.position - lastVelocityData.position
	val deltaRotation = currentVelocityData.rotation / lastVelocityData.rotation
	return Velocity(
		linear = deltaPosition / deltaTime,
		angular = currentVelocityData.rotation.sandwich(deltaRotation.toRotationVectorQ()) / deltaTime,
	)
}

// At least 1. >1 will make it smoother but less reactive.
private const val SMOOTHING_MULTIPLIER = 3f

// We smooth out the velocity since if a tracker is sending at 100tps and skeleton is at 500hz,
//  4 frames out of 5 will have little to no velocity, so we need to smooth at least across those frames.
private fun smoothVelocity(currentVelocity: Velocity, lastVelocity: Velocity, deltaTime: Float, expectedTps: UShort): Velocity {
	val t = (deltaTime / (SMOOTHING_MULTIPLIER / expectedTps.toFloat())).coerceAtMost(1f)
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
	private val lastVelocities: BodyPartMap<Velocity> = bodyPartMap()
	private val lastVelocityData: BodyPartMap<VelocityData> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableComputedSkeleton: ComputedSkeleton, inputSkeleton: InputSkeleton) {
		// One clock read for the whole pass, so every bone shares the same interval
		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds
		lastProcessTime = now

		mutableComputedSkeleton.forEachBone { part, bone ->
			// The expected TPS for the bone is its input's or its first parent that has one.
			// If there's no available expected TPS, that means there's no movement to derive velocity from anyways.
			val expectedTps = inputSkeleton[part]?.expectedTps
				?: inputSkeleton[part.findFirstParent { inputSkeleton[it]?.expectedTps != null }]?.expectedTps
				?: return@forEachBone

			// Compute current velocity
			val currentVelocityData = VelocityData(bone.rotation, bone.tailPosition)
			val currentVelocity = lastVelocityData[part]?.let { computeVelocity(currentVelocityData, it, deltaTime) } ?: ZERO_VELOCITY

			// Smooth velocity before setting it
			val newVelocity = smoothVelocity(currentVelocity, lastVelocities[part] ?: ZERO_VELOCITY, deltaTime, expectedTps)
			mutableComputedSkeleton[part] = bone.copy(velocity = newVelocity)

			lastVelocityData[part] = currentVelocityData
			lastVelocities[part] = newVelocity
		}
	}

	override fun reset(resetType: ResetType) {
		lastVelocities.clear()
		lastVelocityData.clear()
	}
}
