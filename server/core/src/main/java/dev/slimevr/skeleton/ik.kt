package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.util.subListOfLast
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.log

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

fun chainOffsetsLength(
	bones: ComputedSkeleton,
	chain: IKChain,
): Double = chain.sumOf { requireBone(bones, it).offset.len().toDouble() }

fun parentRot(
	bones: ComputedSkeleton,
	bodyPart: BodyPart,
) = parentOf(bodyPart)?.let { parent ->
	bones[parent]?.rotation
} ?: Quaternion.IDENTITY

const val PREFERRED_ROTATION_START_BIAS = 25.0
const val PREFERRED_ROTATION_START_BIAS_MULTIPLIER = (PREFERRED_ROTATION_START_BIAS * PREFERRED_ROTATION_START_BIAS) - PREFERRED_ROTATION_START_BIAS
fun adjustToPreferredRotations(
	mutableBoneInputs: InputSkeleton,
	mutableComputedSkeleton: ComputedSkeleton,
	goals: List<IKChainGoal>,
) {
	val changedBodyParts = mutableSetOf<BodyPart>()
	for (goal in goals) {
		val distanceFromTarget = chainDistanceFromTarget(mutableComputedSkeleton, goal.chain, goal.target).toDouble()
		val maxChainLength = chainOffsetsLength(mutableComputedSkeleton, goal.chain)
		val chainStartToTarget = (goal.target - requireBone(mutableComputedSkeleton, goal.chain.first()).headPosition).len()
		val distanceRatio = if (chainStartToTarget < maxChainLength) {
			(distanceFromTarget / maxChainLength).coerceAtMost(1.0)
		} else {
			// Unreachable
			continue
		}
		val t = (log(PREFERRED_ROTATION_START_BIAS + (distanceRatio * PREFERRED_ROTATION_START_BIAS_MULTIPLIER), PREFERRED_ROTATION_START_BIAS) - 1).toFloat()
		for (bodyPart in goal.chain) {
			val preferredRotation = BODY_PART_PREFERRED_ROTATION[bodyPart] ?: continue
			val bone = mutableBoneInputs[bodyPart] ?: continue
			mutableBoneInputs[bodyPart] = bone.copy(rotation = bone.rotation * preferredRotation.pow(t))
			changedBodyParts.add(bodyPart)
		}
	}
	if (changedBodyParts.isNotEmpty()) buildBones(mutableComputedSkeleton, mutableBoneInputs, changedBodyParts)
}

// Bones are most likely opposite along the X axis
private val oppositeRotation = Quaternion.rotationAroundXAxis(FastMath.PI)
fun fromChainToTarget(
	bodyPart: BodyPart,
	bones: ComputedSkeleton,
	chain: IKChain,
	target: Vector3,
): Quaternion? {
	val bone = requireBone(bones, bodyPart)
	val chainTail = requireBone(bones, chain.last()).tailPosition

	val localChainTail = chainTail - bone.headPosition
	val localChainTailLenSq = localChainTail.lenSq()
	if (FastMath.isApproxZero(localChainTailLenSq)) {
		// Chain tail is at the origin
		return null
	}

	val localTarget = target - bone.headPosition
	val localTargetLenSq = localTarget.lenSq()
	if (FastMath.isApproxZero(localTargetLenSq)) {
		// Target is at the origin
		return null
	}

	val offset = Quaternion.fromTo(localChainTail.unit(), localTarget.unit())
	return if (FastMath.isApproxEqual(offset.lenSq(), 1f)) {
		offset
	} else {
		// When the vectors are exactly opposite, arbitrarily choose an axis
		alignOffset(bone.rotation.inv(), oppositeRotation)
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
		val rotation = mutableComputedSkeleton[bodyPart]?.rotation ?: return@let null
		alignOffset(
			rotation.inv(),
			it.apply(alignOffset(rotation, offset)),
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
	// If performance is bad, we can combine buildBones calls for non-overlapping IK chains
	buildBones(mutableComputedSkeleton, mutableBoneInputs, chain)
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

	// Set each bone we're gonna do IK on to its preferred rotation
	adjustToPreferredRotations(mutableBoneInputs, mutableComputedSkeleton, goals)

	// IK iterations
	var allGoalsMet = true
	for (i in 0 until maxIterations) {
		allGoalsMet = true
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

	return allGoalsMet
}
