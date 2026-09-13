package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

fun requireBone(bones: ComputedSkeleton, boneId: BoneId) = requireNotNull(bones[boneId]) {
	"The computed skeleton is missing \"${bones.registry[boneId]?.key ?: boneId}\" from the IK chain."
}

fun chainDistanceFromTarget(
	bones: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
): Float {
	val chainTail = requireBone(bones, chain.last()).tailPosition
	return (target - chainTail).len()
}

private val oppositeRotation = Quaternion.rotationAroundZAxis(FastMath.PI)
fun fromChainToTarget(
	boneId: BoneId,
	bones: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
): Quaternion? {
	val boneHead = requireBone(bones, boneId).headPosition
	val chainTail = requireBone(bones, chain.last()).tailPosition

	val localChainTail = (chainTail - boneHead).unit()
	if (FastMath.isApproxEqual(localChainTail.lenSq(), 0f)) {
		// Chain tail is at the origin
		return null
	}

	val localTarget = (target - boneHead).unit()
	if (FastMath.isApproxEqual(localTarget.lenSq(), 0f)) {
		// Target is at the origin
		return null
	}

	val offset = Quaternion.fromTo(localChainTail, localTarget)
	return if (FastMath.isApproxEqual(offset.lenSq(), 1f)) {
		offset
	} else {
		// When the vectors are exactly opposite, arbitrarily choose an axis
		oppositeRotation
	}
}

fun rotateChain(
	boneInputs: InputSkeleton,
	chain: IKChain,
	rotation: Quaternion,
) {
	for (boneId in chain) {
		val boneInput = requireNotNull(boneInputs[boneId]) {
			"The provided bone inputs are missing \"${boneInputs.registry[boneId]?.key ?: boneId}\" from the IK chain."
		}
		boneInputs[boneId] = boneInput.copy(rotation = rotation * boneInput.rotation)
	}
}

fun ccdIkIteration(
	boneInputs: InputSkeleton,
	bones: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
	constraints: Map<BoneId, Constraint>?,
): ComputedSkeleton {
	// TODO: Do we need annealing and/or dampening?
	// The first bone in the chain is the one we are adjusting in this iteration
	val boneId = chain.first()
	val offset = fromChainToTarget(boneId, bones, chain, target) ?: return bones

	// We only need to constrain the bone that we are adjusting
	val constrainedOffset = constraints?.let {
		constrainOffsetWithSkeleton(
			boneId,
			offset,
			bones,
			it,
		)
	} ?: offset

	// Mutate the input skeleton
	rotateChain(boneInputs, chain, constrainedOffset)

	// Only build bones for inputs that were changed
	return buildBones(boneInputs, BoneSet.of(boneInputs.registry, chain), bones)
}

typealias IKChain = List<BoneId>
data class IKChainGoal(
	val chain: IKChain,
	val target: Vector3,
)

data class IKOutput(
	val bones: ComputedSkeleton,
	val goalsReached: Map<IKChainGoal, Boolean>,
)

fun ccdIk(
	boneInputs: InputSkeleton,
	bones: ComputedSkeleton,
	goals: List<IKChainGoal>,
	constraints: Map<BoneId, Constraint>?,
	threshold: Float,
	maxIterations: Int,
): IKOutput {
	// No goals leaves every bone as the FK pass built it, so nothing below needs to run
	if (goals.isEmpty()) return IKOutput(bones, emptyMap())

	val workingBoneInputs = boneInputs.copy()
	var boneOutputs = bones

	for (i in 0..maxIterations) {
		// Iterate chains not meeting the threshold
		boneOutputs = goals.filter {
			chainDistanceFromTarget(bones, it.chain, it.target) > threshold
		}.ifEmpty {
			break
		}.fold(boneOutputs) { bones, goal ->
			// The chain from the current bone to the end, iterating backwards
			val iterationChain = goal.chain.takeLast((i % goal.chain.size) + 1)
			ccdIkIteration(
				workingBoneInputs,
				bones,
				iterationChain,
				goal.target,
				constraints,
			)
		}
	}

	return IKOutput(
		boneOutputs,
		goals.associateWith {
			chainDistanceFromTarget(boneOutputs, it.chain, it.target) <= threshold
		},
	)
}
