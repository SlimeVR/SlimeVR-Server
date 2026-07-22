package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.resolveRotationFor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating bones' yaw and roll to match other bones' yaw and roll.
 */
class BoneYawRollAlignProcessor(val settings: Settings) : SkeletonProcessor {
	/**
	 * First value is the BodyPart to be aligned.
	 *
	 * Second value is a Pair containing a list of BodyParts to align with as well as a boolean specifying
	 * whether the BodyPart to be aligned must be inactive.
	 */
	private val bodyPartToSources = mapOf(
		BodyPart.HIP to (setOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG) to true),
		BodyPart.LEFT_UPPER_LEG to (setOf(BodyPart.LEFT_LOWER_LEG) to false),
		BodyPart.RIGHT_UPPER_LEG to (setOf(BodyPart.RIGHT_LOWER_LEG) to false),
	)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs
		val ratios = settings.context.state.value.data.skeletonConfig.ratios
		val bodyPartToMixFactor = mapOf(
			BodyPart.HIP to ratios.interpolateHipWithUpperLegs,
			BodyPart.LEFT_UPPER_LEG to ratios.interpolateUpperLegsWithLowerLegs,
			BodyPart.RIGHT_UPPER_LEG to ratios.interpolateUpperLegsWithLowerLegs,
		)

		val updatedAlignedBones = bodyPartToSources.keys.associateWith { bodyPart ->
			val bone = boneInputs.getValue(bodyPart)
			val sources = bodyPartToSources.getValue(bodyPart)
			if (sources.second && bone.isActive) return@associateWith bone
			val mixFactor = bodyPartToMixFactor[bodyPart] ?: return@associateWith bone

			val sourceRotation = boneInputs.resolveRotationFor(sources.first)
			val aligned = alignYawRoll(bone.rawRotation, sourceRotation)
			val newRotation = bone.rawRotation.interpR(aligned, mixFactor)

			bone.copy(rawRotation = newRotation)
		}

		return state.copy(boneInputs = boneInputs + updatedAlignedBones)
	}

	/**
	 * Rotates the first Quaternion to match its yaw and roll to the rotation of
	 * the second Quaternion
	 *
	 * @param from the first Quaternion
	 * @param to the second Quaternion
	 * @return the rotated Quaternion
	 */
	private fun alignYawRoll(from: Quaternion, to: Quaternion): Quaternion {
		val r = from.inv() * to
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return (from * r * c).unit()
	}
}
