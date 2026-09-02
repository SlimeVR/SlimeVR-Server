package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating the upper legs' roll to match the lower legs' roll.
 */
class UpperLegsRollAlignInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	/**
	 * First value is the BodyPart to be aligned.
	 *
	 * Second value is a source BodyPart
	 */
	private val bodyPartToSources = arrayOf(
		BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_LOWER_LEG,
	)

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs
		if (ratio == 0f) return

		// Upper legs are written, lower legs are read, so the two never overlap
		for (bodyPartToSource in bodyPartToSources) {
			val bone = inputSkeleton[bodyPartToSource.first] ?: continue
			if (!bone.isRotationActive) continue

			val sourceRotation = inputSkeleton[bodyPartToSource.second]?.rotation ?: continue
			val alignedRotation = alignRoll(bone.rotation, sourceRotation)
			inputSkeleton[bodyPartToSource.first] = bone.copy(rotation = bone.rotation.interpQ(alignedRotation, ratio))
		}
	}

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
}
