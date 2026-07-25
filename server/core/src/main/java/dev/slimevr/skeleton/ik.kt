package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun chainDistanceFromTarget(bones: Map<BodyPart, BoneState>, chain: List<BodyPart>, target: Vector3): Float {
	val endBone = bones[chain.last()]
	requireNotNull(endBone)
	return (target - endBone.tailPosition).len()
}

fun chainCanReach(bones: Map<BodyPart, BoneState>, chain: List<BodyPart>, target: Vector3): Boolean {
	val rootBone = bones[chain.first()]
	requireNotNull(rootBone)
	val chainLength = chain.fold(0f) { acc, bodyPart ->
		val currentBone = requireNotNull(bones[bodyPart])
		acc + currentBone.offset.len()
	}
	return (target - rootBone.headPosition).len() <= chainLength
}

// Single loop over a chain
fun ccdIk(boneInputs: BodyPartMap<BoneInput>, bones: Map<BodyPart, BoneState>, chain: List<BodyPart>, target: Vector3): BodyPartMap<BoneState> {
	val workingBones = BodyPartMap(boneInputs)

	// Reversed index, i increases backwards up the chain
	for ((i, bodyPart) in chain.reversed().withIndex()) {
		val currentRawBone = requireNotNull(boneInputs[bodyPart])
		val currentBone = requireNotNull(bones[bodyPart])

		val localOffset = currentBone.localTailPosition
		val localTarget = target - currentBone.headPosition
		val rotationChange = Quaternion.fromTo(localOffset, localTarget).pow(
			(i.toFloat() / chain.size) * 0.5f,
		).unit()

		workingBones[bodyPart] = currentRawBone.copy(
			rawRotation = currentBone.rotation * rotationChange,
		)
	}

	// FIXME: This feels weird, we should probably consume the skeleton root position
	//  independent of the bones
	val skeletonRootPos = bones[BodyPart.HEAD]?.headPosition ?: Vector3.NULL
	return buildBones(workingBones, skeletonRootPos)
}
