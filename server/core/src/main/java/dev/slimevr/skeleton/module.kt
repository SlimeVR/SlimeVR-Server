package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

data class BoneState(
	val bodyPart: BodyPart,
	val length: Float,
	val rotation: Quaternion,
	val headPosition: Vector3,
	val tailPosition: Vector3,
	val parentBone: BoneState?,
) {
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
	val localTailPosition: Vector3
		get() = tailPosition - headPosition
}

data class SkeletonState(
	val bones: Map<BodyPart, BoneState>,
	val rootBone: BoneState,
)

val DEFAULT_SKELETON_STATE = run {
	val skeletonBones = mutableMapOf<BodyPart, BoneState>()
	iterateBoneHierarchy().forEach { (parent, child) ->
		val parent = skeletonBones[parent]

		val length = when (child) {
			BodyPart.HEAD, BodyPart.NECK -> 0.1f

			BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER -> 0.175f

			BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM,
			-> 0.26f

			BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND -> 0.13f

			BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL,
			BodyPart.LEFT_INDEX_DISTAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_PROXIMAL,
			BodyPart.LEFT_MIDDLE_DISTAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_PROXIMAL,
			BodyPart.LEFT_RING_DISTAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_PROXIMAL,
			BodyPart.LEFT_LITTLE_DISTAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_PROXIMAL,

			BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL,
			BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL,
			BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
			BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL,
			BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
			-> 0.025f

			BodyPart.UPPER_CHEST, BodyPart.CHEST -> 0.16f

			BodyPart.WAIST -> 0.2f

			BodyPart.HIP -> 0.04f

			BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP -> 0.13f

			BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG -> 0.42f

			BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG -> 0.5f

			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> 0.05f

			else -> 0.1f
		}
		val rot = when (child) {
			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
			else -> Quaternion.IDENTITY
		}
		val head = parent?.tailPosition ?: Vector3.NULL
		val offset = when (child) {
			BodyPart.HEAD -> Vector3(0f, 0f, length)
			BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND -> Vector3(0f, 0f, -length)
			BodyPart.LEFT_SHOULDER -> Vector3(-length, -0.08f, 0f)
			BodyPart.RIGHT_SHOULDER -> Vector3(length, -0.08f, 0f)
			BodyPart.LEFT_HIP -> Vector3(-length, 0f, 0f)
			BodyPart.RIGHT_HIP -> Vector3(length, 0f, 0f)
			else -> Vector3(0f, -length, 0f)
		}

		val bone = BoneState(
			bodyPart = child,
			length = length,
			rotation = rot,
			headPosition = head,
			tailPosition = head + offset,
			parentBone = parent,
		)
		skeletonBones[child] = bone
	}

	SkeletonState(
		bones = skeletonBones,
		rootBone = skeletonBones[BodyPart.HEAD]!!,
	)
}
