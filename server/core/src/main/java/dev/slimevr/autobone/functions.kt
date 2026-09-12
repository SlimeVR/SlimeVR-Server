package dev.slimevr.autobone

import dev.slimevr.skeleton.ComputedSkeleton
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun slideContributionWeight(ankleA: Vector3, ankleB: Vector3, localBoneTailA: Vector3, localBoneTailB: Vector3): Float {
	val ankleAToB = ankleB - ankleA
	val localBoneTailAToB = localBoneTailB - localBoneTailA

	val boneToSlideDirectionRatio = ankleAToB.unit().dot(localBoneTailAToB.unit())

	// if +, shorten bone, if -, lengthen bone
	// sum all contribution weights, divide each bone's weight by the sum, then multiply
	//  by the slide magnitude in order to find the corresponding bone change
	return localBoneTailAToB.len() * -boneToSlideDirectionRatio
}

// TODO This is a placeholder
val PLACEHOLDER_BODY_PARTS_TO_ADJUST = arrayOf(
	BodyPart.UPPER_CHEST,
	BodyPart.LOWER_CHEST,
	BodyPart.UPPER_WAIST,
	BodyPart.LOWER_WAIST,
	BodyPart.HIP,
	// etc.
)

fun step(skeletonA: ComputedSkeleton, skeletonB: ComputedSkeleton) {
	// TODO We need to consider both ankles, there are two... We can do contribution per
	//  ankle
	// TODO This is a placeholder
	val ankleA = Vector3.ZERO
	// TODO This is a placeholder
	val ankleB = Vector3.ZERO

	// TODO Consider headset sliding; if HMD consistently aligns with sliding, then
	//  either height is wrong, or the headset has weird movement
	val contributions = PLACEHOLDER_BODY_PARTS_TO_ADJUST.map { bodyPart ->
		val boneA = skeletonA[bodyPart] ?: return@map 0f
		val boneB = skeletonB[bodyPart] ?: return@map 0f

		slideContributionWeight(
			ankleA,
			ankleB,
			boneA.localTailPosition,
			boneB.localTailPosition,
		)
	}.filter { it > 0f }

	val contributionSum = contributions.sum()
	val normalizedContributions = contributions.map {
		it / contributionSum
	}

	// TODO This is a placeholder
	val config = 1f
	// TODO This is a placeholder
	val contribution = 1f
	val adjustVal = contribution * config

	// TODO Apply config
	val newConfig = config + adjustVal

	// TODO Test new config to see if it actually reduces error? Or do we just
	//  accept the changes? The contribution scales for each frame and considers
	//  direction; we should be able to skip checking if it reduces error
}
