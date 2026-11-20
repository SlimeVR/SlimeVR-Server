package dev.slimevr.autobone

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import dev.slimevr.util.Side
import dev.slimevr.util.side
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

val BODY_PARTS_TO_ADJUST = arrayOf(
	BodyPart.NECK,
	BodyPart.UPPER_CHEST,
	BodyPart.LOWER_CHEST,
	BodyPart.UPPER_WAIST,
	BodyPart.LOWER_WAIST,
	BodyPart.HIP,

	BodyPart.LEFT_UPPER_LEG,
	BodyPart.LEFT_LOWER_LEG,

	BodyPart.RIGHT_UPPER_LEG,
	BodyPart.RIGHT_LOWER_LEG,
)

// TODO This should probably be every bone excluding arms, feet, and toes?
val CONTRIBUTING_BODY_PARTS = BODY_PARTS_TO_ADJUST.plus(
	arrayOf(
		BodyPart.HEAD,
	),
)

data class TrackerFrame(
	val rotation: Quaternion,
	val position: Vector3?,
)
typealias AutoBoneRecording = List<TrackerFrame>
// TODO Figure out actually doing the recording

typealias BoneContribution = WeightedAverage
typealias BoneContributions = BodyPartMap<BoneContribution>

fun process(recording: BodyPartMap<AutoBoneRecording>) {
	// TODO Actually make skeletons
	val skeletonA: ComputedSkeleton = bodyPartMap()
	val skeletonB: ComputedSkeleton = bodyPartMap()

	val contributions: BoneContributions = bodyPartMap()

	val recordingLength = recording.minOf { it.value.size }
	for (i in 0 until recordingLength) {
		val trackers = recording.mapValues { _, frames -> frames[i] }
		// TODO Apply trackers to skeletons A and B somehow

		computeContributions(contributions, skeletonA, skeletonB)

		// TODO Figure out how to choose adjustment scale
		// TODO Test new config to see if it actually reduces error
	}
}

fun boneContribution(contribution: BoneContribution, ankleAToB: Vector3, localBoneTailAToB: Vector3) {
	val boneToSlideDirectionRatio = ankleAToB.unit().dot(localBoneTailAToB.unit())
	// - if bone is too short, + if bone is too long
	contribution.addValue(boneToSlideDirectionRatio, localBoneTailAToB.len())
}

fun computeContributions(contributions: BoneContributions, skeletonA: ComputedSkeleton, skeletonB: ComputedSkeleton) {
	val leftAnkleA = skeletonA[BodyPart.LEFT_LOWER_LEG]?.tailPosition ?: error("Missing left ankle position A")
	val leftAnkleB = skeletonB[BodyPart.LEFT_LOWER_LEG]?.tailPosition ?: error("Missing left ankle position B")
	val leftAnkleAToB = leftAnkleB - leftAnkleA

	val rightAnkleA = skeletonA[BodyPart.RIGHT_LOWER_LEG]?.tailPosition ?: error("Missing right ankle position A")
	val rightAnkleB = skeletonB[BodyPart.RIGHT_LOWER_LEG]?.tailPosition ?: error("Missing right ankle position B")
	val rightAnkleAToB = rightAnkleB - rightAnkleA

	// TODO Consider headset sliding; if HMD consistently aligns with sliding, then
	//  either height is wrong, or the headset has weird movement
	CONTRIBUTING_BODY_PARTS.forEach { bodyPart ->
		val boneA = skeletonA[bodyPart] ?: return@forEach
		val boneB = skeletonB[bodyPart] ?: return@forEach

		val contribution = contributions.getOrPut(bodyPart) { BoneContribution() }

		val localBoneTailAToB = boneB.localTailPosition - boneA.localTailPosition
		val side = bodyPart.side
		// LEFT (or both)
		if (side != Side.RIGHT) {
			boneContribution(
				contribution,
				leftAnkleAToB,
				localBoneTailAToB,
			)
		}
		// RIGHT (or both)
		if (side != Side.LEFT) {
			boneContribution(
				contribution,
				rightAnkleAToB,
				localBoneTailAToB,
			)
		}
	}
}
