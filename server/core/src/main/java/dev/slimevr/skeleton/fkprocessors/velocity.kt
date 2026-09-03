package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.time.ComparableTimeMark

/**
 * Computes linear (m/s) and angular (rad/s) velocity for the bones.
 */
class VelocityFkProcessor : SkeletonFkProcessor {
	private data class VelocityBoneData(
		val rotation: Quaternion,
		val tailPosition: Vector3,
	)

	private val lastVelocityBoneData: BodyPartMap<VelocityBoneData> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	private fun computeVelocity(
		currentBone: BoneState,
		lastVelocityData: VelocityBoneData,
		lastTime: ComparableTimeMark,
	): Velocity {
		val deltaPosition = currentBone.tailPosition - lastVelocityData.tailPosition
		val deltaRotation = currentBone.rotation / lastVelocityData.rotation
		val deltaTime = (timeSource.markNow() - lastTime).inFloatingSeconds
		return Velocity(
			linear = deltaPosition / deltaTime,
			angular = deltaRotation.toRotationVector() / deltaTime,
		)
	}

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		fk.forEachBone { part, bone ->
			val lastVelocityData = lastVelocityBoneData[part]
			if (lastVelocityData != null) {
				val velocity = computeVelocity(bone, lastVelocityData, lastProcessTime)
				mutableInputSkeleton[part] = mutableInputSkeleton[part]?.copy(velocity = velocity)
			}

			lastVelocityBoneData[part] = VelocityBoneData(bone.rotation, bone.tailPosition)
		}
		lastProcessTime = timeSource.markNow()
	}
}
