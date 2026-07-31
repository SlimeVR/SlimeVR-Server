package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.mutate
import dev.slimevr.skeleton.resolveAverageRotationFor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating bones' yaw and roll to match other bones' yaw and roll.
 */
class BoneYawRollAlignProcessor(val settings: Settings) : SkeletonProcessor {
	private class SourceLink(val bodyPart: BodyPart, val sources: Array<BodyPart>, val mustBeActive: Boolean)

	/**
	 * First value is the BodyPart to be aligned.
	 *
	 * Second value is a SourceLink containing a list of BodyParts to align with as well as a boolean specifying
	 * whether the BodyPart to be aligned must be inactive.
	 */
	private val bodyPartToSources = arrayOf(
		SourceLink(BodyPart.HIP, arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG), mustBeActive = false),
		SourceLink(BodyPart.LEFT_UPPER_LEG, arrayOf(BodyPart.LEFT_LOWER_LEG), mustBeActive = true),
		SourceLink(BodyPart.RIGHT_UPPER_LEG, arrayOf(BodyPart.RIGHT_LOWER_LEG), mustBeActive = true),
	)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs
		val ratios = settings.context.state.value.data.skeletonConfig.ratios

		val updatedAlignedBones = boneInputs.mutate { updated ->
			for (link in bodyPartToSources) {
				val bone = boneInputs.getValue(link.bodyPart)
				if (bone.isActive != link.mustBeActive) continue

				val mixFactor = when (link.bodyPart) {
					BodyPart.HIP -> ratios.interpolateHipWithUpperLegs
					else -> ratios.interpolateUpperLegsWithLowerLegs
				}
				val sourceRotation = boneInputs.resolveAverageRotationFor(link.sources)
				val aligned = alignYawRoll(bone.rawRotation, sourceRotation)
				updated[link.bodyPart] = bone.copy(rawRotation = bone.rawRotation.interpR(aligned, mixFactor))
			}
		}

		return state.copy(boneInputs = updatedAlignedBones)
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
