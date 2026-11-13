package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Rotates the first Quaternion to match its roll to the rotation of
 * the second Quaternion
 *
 * @param from the first Quaternion
 * @param to the second Quaternion
 * @return the rotated Quaternion
 */
private fun alignRoll(from: Quaternion, to: Quaternion): Quaternion {
	val local = to.localAbout(from)
	val roll = local.project(Vector3.POS_Y).unit()
	return roll.globalAbout(from)
}

/**
 * Handles rotating the upper legs' roll to match the lower legs' roll.
 */
class UpperLegsTwistInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	/**
	 * First value is the BodyPart to be aligned.
	 * Second value is the source BodyPart.
	 */
	private val bodyPartToSources = arrayOf(
		BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_LOWER_LEG,
	)

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs
		if (ratio == 0f) return

		// Upper legs are written, lower legs are read, so the two never overlap
		for (bodyPartToSource in bodyPartToSources) {
			val bone = mutableInputSkeleton[bodyPartToSource.first] ?: continue
			if (!bone.isRotationActive) continue

			val sourceRotation = mutableInputSkeleton[bodyPartToSource.second]?.rotation ?: continue
			val alignedRotation = alignRoll(bone.rotation, sourceRotation)
			mutableInputSkeleton[bodyPartToSource.first] = bone.copy(rotation = bone.rotation.interpR(alignedRotation, ratio))
		}
	}
}
