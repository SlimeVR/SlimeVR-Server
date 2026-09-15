package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.boneId
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion

/**
 * Rotates the first Quaternion to match its roll to the rotation of
 * the second Quaternion
 *
 * @param from the first Quaternion
 * @param to the second Quaternion
 * @return the rotated Quaternion
 */
private fun alignRoll(from: Quaternion, to: Quaternion): Quaternion {
	val r = to.inv() * from
	val c = Quaternion(r.w, 0f, -r.y, 0f)
	return (to * r * c).unit()
}

/**
 * Handles rotating the upper legs' roll to match the lower legs' roll.
 */
class UpperLegsRollAlignInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	/**
	 * First value is the bone to be aligned.
	 *
	 * Second value is a source bone.
	 */
	private val boneToSources: Array<Pair<BoneId, BoneId>> = arrayOf(
		BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_LOWER_LEG,
	).map { (bodyPart, source) -> bodyPart.boneId to source.boneId }.toTypedArray()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs
		if (ratio == 0f) return

		// Upper legs are written, lower legs are read, so the two never overlap
		for ((boneId, sourceId) in boneToSources) {
			val bone = mutableInputSkeleton[boneId] ?: continue
			if (!bone.isRotationActive) continue

			val sourceRotation = mutableInputSkeleton[sourceId]?.rotation ?: continue
			val alignedRotation = alignRoll(bone.rotation, sourceRotation)
			mutableInputSkeleton[boneId] = bone.copy(rotation = bone.rotation.interpR(alignedRotation, ratio))
		}
	}
}
