package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.util.subListOfLast
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

fun parentRot(
	bones: ComputedSkeleton,
	bodyPart: BodyPart,
) = parentOf(bodyPart)?.let { parent ->
	bones[parent]?.rotation
} ?: Quaternion.IDENTITY

// Bones are most likely opposite along the X axis
private val oppositeRotation = Quaternion.rotationAroundXAxis(FastMath.PI)
private val smallRotation = Quaternion.rotationAroundXAxis(1f * FastMath.DEG_TO_RAD)
fun fromChainToTarget(
	bodyPart: BodyPart,
	bones: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
): Quaternion? {
	val boneHead = requireBone(bones, bodyPart).headPosition
	val chainTail = requireBone(bones, chain.last()).tailPosition

	val localChainTail = chainTail - boneHead
	val localChainTailLenSq = localChainTail.lenSq()
	if (FastMath.isApproxZero(localChainTailLenSq)) {
		// Chain tail is at the origin
		return null
	}

	val localTarget = target - boneHead
	val localTargetLenSq = localTarget.lenSq()
	if (FastMath.isApproxZero(localTargetLenSq)) {
		// Target is at the origin
		return null
	}

	val offset = Quaternion.fromTo(localChainTail.unit(), localTarget.unit())

	return if (localTargetLenSq < localChainTailLenSq &&
		FastMath.isApproxZero(offset.angleQ())
	) {
		// TODO Should be based off a preferred axis/direction, currently seems to
		//  "snap" a little when hitting this fallback
		// If the target is closer than the tail and the bone is parallel to the target,
		//  apply an arbitrary small rotation to promote movement
		alignOffset(parentRot(bones, bodyPart).inv(), smallRotation)
	} else if (FastMath.isApproxEqual(offset.lenSq(), 1f)) {
		offset
	} else {
		// When the vectors are exactly opposite, arbitrarily choose an axis
		alignOffset(parentRot(bones, bodyPart).inv(), oppositeRotation)
	}
}

// Apply offset in global space to rotation
fun applyOffset(
	rotation: Quaternion,
	offset: Quaternion,
) = offset * rotation

fun computeOffset(
	rotation: Quaternion,
	rotationWithOffset: Quaternion,
) = rotationWithOffset / rotation

fun alignOffset(
	rotation: Quaternion,
	offset: Quaternion,
) = rotation.inv() * offset * rotation

fun constrainOffsetWithSkeleton(
	bodyPart: BodyPart,
	offset: Quaternion,
	bones: ComputedSkeleton,
	constraint: Constraint,
): Quaternion {
	val boneRot = bones[bodyPart]?.rotation ?: return offset

	return computeOffset(
		boneRot,
		constraint.apply(
			parentRot(bones, bodyPart),
			applyOffset(boneRot, offset),
		),
	)
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
				rotation = applyOffset(boneInput.rotation, rotation),
			)
		}
	}
}

fun ccdIkIteration(
	mutableBoneInputs: InputSkeleton,
	mutableComputedSkeleton: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
	offsetConstraint: Constraint?,
	constraint: Constraint?,
) {
	// TODO: Do we need annealing and/or dampening?
	// The first bone in the chain is the one we are adjusting in this iteration
	val bodyPart = chain.first()
	val offset = fromChainToTarget(bodyPart, mutableComputedSkeleton, chain, target) ?: return

	// Constrain the local offset
	val localConstrainedOffset = offsetConstraint?.let {
		val parentRot = parentRot(mutableComputedSkeleton, bodyPart)
		alignOffset(
			parentRot.inv(),
			it.apply(alignOffset(parentRot, offset)),
		)
	} ?: offset

	// We only need to constrain the bone that we are adjusting
	val constrainedOffset = constraint?.let {
		constrainOffsetWithSkeleton(
			bodyPart,
			localConstrainedOffset,
			mutableComputedSkeleton,
			it,
		)
	} ?: localConstrainedOffset

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

/**
 * @return True if all goals were met, false if any goal was not met.
 */
fun ccdIk(
	mutableBoneInputs: InputSkeleton,
	mutableComputedSkeleton: ComputedSkeleton,
	goals: List<IKChainGoal>,
	offsetConstraints: BodyPartMap<Constraint>?,
	constraints: BodyPartMap<Constraint>?,
	threshold: Float,
	maxIterations: Int,
): Boolean {
	// No goal leaves every bone as the FK pass built it, so nothing below needs to run
	if (goals.isEmpty()) return true

	for (i in 0 until maxIterations) {
		var allGoalsMet = true
		for ((chain, target) in goals) {
			// Only iterate chains not meeting the threshold
			if (chainDistanceFromTarget(mutableComputedSkeleton, chain, target) <= threshold) continue
			allGoalsMet = false

			// The chain from the current bone to the end, iterating backwards
			// TODO We can avoid making any objects or just cache this if it's slow,
			//  check performance to see if needed
			val iterationChain = chain.subListOfLast((i % chain.size) + 1)
			val bodyPart = iterationChain.first()
			ccdIkIteration(
				mutableBoneInputs,
				mutableComputedSkeleton,
				iterationChain,
				target,
				offsetConstraints?.get(bodyPart),
				constraints?.get(bodyPart),
			)
		}
		if (allGoalsMet) break
	}

	return goals.all { (chain, target) ->
		chainDistanceFromTarget(mutableComputedSkeleton, chain, target) <= threshold
	}
}
