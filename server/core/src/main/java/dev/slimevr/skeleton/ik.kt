package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun chainDistanceFromTarget(bones: ComputedSkeleton, chain: List<BodyPart>, target: Vector3): Float {
	val endBone = bones[chain.last()]
	requireNotNull(endBone)
	return (target - endBone.tailPosition).len()
}

fun chainCanReach(bones: ComputedSkeleton, chain: List<BodyPart>, target: Vector3): Boolean {
	val rootBone = bones[chain.first()]
	requireNotNull(rootBone)
	val chainLength = chain.fold(0f) { acc, bodyPart ->
		val currentBone = requireNotNull(bones[bodyPart])
		acc + currentBone.offset.len()
	}
	return (target - rootBone.headPosition).len() <= chainLength
}

fun ccdIkIteration(boneInputs: InputSkeleton, bones: ComputedSkeleton, chain: List<BodyPart>, target: Vector3): ComputedSkeleton {
	val workingBones = boneInputs.toMutableMap()

	var runningRotation = Quaternion.IDENTITY
	for (bodyPart in chain) {
		val currentRawBone = requireNotNull(boneInputs[bodyPart])
		val currentBone = requireNotNull(bones[bodyPart])

		val localOffset = currentBone.localTailPosition.unit()
		val localTarget = (target - currentBone.headPosition).unit()
		if (!FastMath.isApproxEqual(localTarget.lenSq(), 1f)) {
			// Avoid impossible condition where the target is at the origin
			continue
		}

		val rotationChange = Quaternion.fromTo(localOffset, localTarget)
		if (!FastMath.isApproxEqual(rotationChange.lenSq(), 1f)) {
			// No change?
			continue
		}

		// TODO: Apply constraints
		workingBones[bodyPart] = currentRawBone.copy(
			rawRotation = runningRotation * currentBone.rotation * rotationChange,
		)
		runningRotation *= rotationChange
	}

	// FIXME: This feels weird, we should probably consume the skeleton root position
	//  independent of the bones
	val skeletonRootPos = bones[BodyPart.HEAD]?.headPosition ?: Vector3.NULL
	return buildBones(workingBones, skeletonRootPos)
}

data class IKChainGoal(
	val chain: List<BodyPart>,
	val target: Vector3,
)

data class IKOutput(
	val bones: ComputedSkeleton,
	val goalsReached: Map<IKChainGoal, Boolean>,
)

fun ccdIk(boneInputs: InputSkeleton, bones: ComputedSkeleton, goals: List<IKChainGoal>, threshold: Float, maxIterations: Int): IKOutput {
	var curBones = bones

	for (i in 0..maxIterations) {
		// Iterate chains not meeting the threshold
		curBones = goals.filter {
			chainDistanceFromTarget(bones, it.chain, it.target) > threshold
		}.ifEmpty {
			break
		}.fold(curBones) { bones, goal ->
			ccdIkIteration(boneInputs, bones, goal.chain, goal.target)
		}
	}

	return IKOutput(
		curBones,
		goals.associateWith {
			chainDistanceFromTarget(curBones, it.chain, it.target) <= threshold
		},
	)
}
