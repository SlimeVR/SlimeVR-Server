package dev.slimevr.skeleton.computedprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonComputedProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Rotates the first Quaternion to match its twist to the rotation of
 * the second Quaternion
 *
 * @param from the first Quaternion
 * @param to the second Quaternion
 * @return the rotated Quaternion
 */
private fun alignTwist(from: Quaternion, to: Quaternion): Quaternion {
	val local = to.localAbout(from)
	val twist = local.eulerHeading()
	return twist.globalAbout(from)
}

/**
 * Handles rotating the upper legs' twist to match the lower legs' twist to make VRChat's knee IK work better.
 *
 * This is a computed processor running after IK because we want to use the real upper leg's twist for IK and constraints.
 */
class UpperLegsTwistComputedProcessor(val settings: Settings) : SkeletonComputedProcessor {
	/**
	 * First value is the BodyPart to be aligned.
	 * Second value is the source BodyPart.
	 */
	private val bodyPartToSources = arrayOf(
		BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_LOWER_LEG,
	)

	override fun process(mutableComputedSkeleton: ComputedSkeleton, inputSkeleton: InputSkeleton) {
		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs
		if (ratio == 0f) return

		// Upper legs are written, lower legs are read, so the two never overlap
		for ((part, source) in bodyPartToSources) {
			val bone = mutableComputedSkeleton[part] ?: continue
			val sourceRotation = mutableComputedSkeleton[source]?.rotation ?: continue

			val alignedRotation = alignTwist(bone.rotation, sourceRotation)
			mutableComputedSkeleton[part] = bone.copy(rotation = bone.rotation.interpR(alignedRotation, ratio))
		}
	}
}
