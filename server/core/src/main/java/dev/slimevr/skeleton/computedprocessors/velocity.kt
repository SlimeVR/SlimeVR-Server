package dev.slimevr.skeleton.computedprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.SkeletonComputedProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

data class VelocityBoneData(
	val rotation: Quaternion,
	val tailPosition: Vector3,
)

fun computeVelocity(
	currentBone: BoneState,
	lastVelocityData: VelocityBoneData,
	deltaTime: Float,
): Velocity {
	val deltaPosition = currentBone.tailPosition - lastVelocityData.tailPosition
	val deltaRotation = currentBone.rotation / lastVelocityData.rotation
	return Velocity(
		linear = deltaPosition / deltaTime,
		angular = deltaRotation.toRotationVector() / deltaTime,
	)
}

/**
 * Computes linear (m/s) and angular (rad/s) velocity for the bones.
 */
class VelocityComputedProcessor : SkeletonComputedProcessor {
	private val lastVelocityBoneData: BodyPartMap<VelocityBoneData> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableComputedSkeleton: ComputedSkeleton, floorLevel: Float) {
		// One clock read for the whole pass, so every bone shares the same interval
		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds
		lastProcessTime = now

		mutableComputedSkeleton.forEachBone { part, bone ->
			val lastVelocityData = lastVelocityBoneData[part]
			if (lastVelocityData != null) {
				mutableComputedSkeleton[part] = bone.copy(velocity = computeVelocity(bone, lastVelocityData, deltaTime))
			}

			lastVelocityBoneData[part] = VelocityBoneData(bone.rotation, bone.tailPosition)
		}
	}
}
