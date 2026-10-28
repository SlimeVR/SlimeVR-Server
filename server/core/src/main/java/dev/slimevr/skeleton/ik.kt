package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun requireBone(bones: ComputedSkeleton, bodyPart: BodyPart) = requireNotNull(bones[bodyPart]) {
	"The computed skeleton is missing \"${bodyPart}\" from the IK chain."
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
	bodyPart: BodyPart,
	bones: ComputedSkeleton,
	chain: IKChain,
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

fun rotateChain(
	boneInputs: InputSkeleton,
	chain: IKChain,
	rotation: Quaternion,
) {
	for (bodyPart in chain) {
		boneInputs.compute(bodyPart) { _, boneInput ->
			requireNotNull(boneInput) {
				"The provided bone inputs are missing \"${bodyPart}\" from the IK chain."
			}.copy(
				rotation = rotation * boneInput.rotation,
			)
		}
	}
}

fun ccdIkIteration(
	mutableBoneInputs: InputSkeleton,
	mutableComputedSkeleton: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
	constraints: BodyPartMap<Constraint>?,
) {
	// TODO: Do we need annealing and/or dampening?
	// The first bone in the chain is the one we are adjusting in this iteration
	val bodyPart = chain.first()
	val offset = fromChainToTarget(bodyPart, mutableComputedSkeleton, chain, target) ?: return

	// We only need to constrain the bone that we are adjusting
	val constrainedOffset = constraints?.let {
		constrainOffsetWithSkeleton(
			bodyPart,
			offset,
			mutableComputedSkeleton,
			it,
		)
	} ?: offset

	// Mutate the input skeleton
	rotateChain(mutableBoneInputs, chain, constrainedOffset)

	// Only build bones for inputs that were changed
	buildBones(mutableComputedSkeleton, mutableBoneInputs, chain.toSet())
}

typealias IKChain = List<BodyPart>
data class IKChainGoal(
	val chain: IKChain,
	val target: Vector3,
)

fun ccdIk(
	mutableBoneInputs: InputSkeleton,
	mutableComputedSkeleton: ComputedSkeleton,
	goals: List<IKChainGoal>,
	constraints: BodyPartMap<Constraint>?,
	threshold: Float,
	maxIterations: Int,
) {
	// No goal leaves every bone as the FK pass built it, so nothing below needs to run
	if (goals.isEmpty()) return

	for (i in 0..maxIterations) {
		var allGoalsMet = true
		for (goal in goals) {
			// Only iterate chains not meeting the threshold
			if (chainDistanceFromTarget(mutableComputedSkeleton, goal.chain, goal.target) <= threshold) continue
			allGoalsMet = false

			// The chain from the current bone to the end, iterating backwards
			val iterationChain = goal.chain.takeLast((i % goal.chain.size) + 1)
			ccdIkIteration(
				mutableBoneInputs,
				mutableComputedSkeleton,
				iterationChain,
				goal.target,
				constraints,
			)
		}
		if (allGoalsMet) break
	}
}
