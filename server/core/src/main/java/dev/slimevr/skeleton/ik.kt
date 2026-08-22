package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import java.util.EnumMap

fun requireBone(bones: ComputedSkeleton, bodyPart: BodyPart) = requireNotNull(bones[bodyPart]) {
	"The computed skeleton is missing \"${bodyPart}\" from the IK chain."
}

fun chainDistanceFromTarget(
	bones: ComputedSkeleton,
	chain: List<BodyPart>,
	target: Vector3,
): Float {
	val chainTail = requireBone(bones, chain.last()).tailPosition
	return (target - chainTail).len()
}

fun chainCanReach(
	bones: ComputedSkeleton,
	chain: List<BodyPart>,
	target: Vector3,
): Boolean {
	val chainHead = requireBone(bones, chain.first()).headPosition

	val chainLength = chain.fold(0f) { acc, bodyPart ->
		val boneLength = requireBone(bones, bodyPart).offset.len()
		acc + boneLength
	}
	return (target - chainHead).len() <= chainLength
}

private val oppositeRotation = Quaternion.rotationAroundZAxis(FastMath.PI)
fun fromChainToTarget(
	bodyPart: BodyPart,
	bones: ComputedSkeleton,
	chain: List<BodyPart>,
	target: Vector3,
): Quaternion? {
	val boneHead = requireBone(bones, bodyPart).headPosition
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

fun constrainOffset(
	constraint: Constraint,
	parent: Quaternion,
	bone: Quaternion,
	offset: Quaternion,
): Quaternion {
	// TODO: Ensure the quaternion multiplication order is correct here
	return constraint.apply(parent, offset * bone) * bone.inv()
}

fun rotateChain(
	boneInputs: InputSkeleton,
	chain: List<BodyPart>,
	rotation: Quaternion,
) {
	for (bodyPart in chain) {
		boneInputs.compute(bodyPart) { _, boneInput ->
			requireNotNull(boneInput) {
				"The provided bone inputs are missing \"${bodyPart}\" from the IK chain."
			}.copy(
				rawRotation = rotation * boneInput.rawRotation,
			)
		}
	}
}

fun ccdIkIteration(
	boneInputs: InputSkeleton,
	bones: ComputedSkeleton,
	chain: List<BodyPart>,
	target: Vector3,
	constraints: BodyPartMap<Constraint>?,
): ComputedSkeleton {
	// TODO: Do we need annealing and/or dampening?
	// The first bone in the chain is the one we are adjusting in this iteration
	val bodyPart = chain.first()
	val offset = fromChainToTarget(bodyPart, bones, chain, target) ?: return bones

	// We only need to constrain the bone that we are adjusting
	val constrainedOffset = constraints?.get(bodyPart)?.let { constraint ->
		constrainOffset(
			constraint,
			parentOf(bodyPart)?.let { parent ->
				bones[parent]?.rotation
			} ?: Quaternion.IDENTITY,
			requireBone(bones, bodyPart).rotation,
			offset,
		)
	} ?: offset

	// Mutate the input skeleton
	rotateChain(boneInputs, chain, constrainedOffset)

	// FIXME: This feels weird, we should probably consume the skeleton root position
	//  independent of the bones
	val skeletonRootPos = bones[BodyPart.HEAD]?.headPosition ?: Vector3.NULL
	return buildBones(boneInputs, skeletonRootPos)
}

data class IKChainGoal(
	val chain: List<BodyPart>,
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
	constraints: BodyPartMap<Constraint>?,
	threshold: Float,
	maxIterations: Int,
): IKOutput {
	val workingBoneInputs = EnumMap(boneInputs)
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
