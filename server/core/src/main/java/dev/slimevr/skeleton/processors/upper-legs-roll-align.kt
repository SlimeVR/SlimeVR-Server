package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.mutate
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating the upper legs' roll to match the lower legs' roll.
 */
class UpperLegsRollAlignProcessor(val settings: Settings) : SkeletonProcessor {
	/**
	 * First value is the BodyPart to be aligned.
	 *
	 * Second value is a source BodyPart
	 */
	private val bodyPartToSources = arrayOf(
		BodyPart.LEFT_UPPER_LEG to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_UPPER_LEG to BodyPart.RIGHT_LOWER_LEG,
	)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs
		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs

		val updatedAlignedBones = boneInputs.mutate { updated ->
			for (bodyPartToSource in bodyPartToSources) {
				val bone = boneInputs.getValue(bodyPartToSource.first)
				if (!bone.isActive) continue

				val sourceRotation = boneInputs.getValue(bodyPartToSource.second).rawRotation
				val alignedRotation = alignRoll(bone.rawRotation, sourceRotation)
				updated[bodyPartToSource.first] = bone.copy(rawRotation = bone.rawRotation.interpQ(alignedRotation, ratio))
			}
		}

		return state.copy(boneInputs = updatedAlignedBones)
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
