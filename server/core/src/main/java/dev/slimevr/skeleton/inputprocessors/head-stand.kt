package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutate
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Moves the head input to the skeleton height
 */
class HeadStandInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton {
		val headBone = inputSkeleton.getValue(BodyPart.HIP)

		if (headBone.isPositionActive || settings.context.state.value.data.skeletonConfig.toggles.mocapMode) {
			return inputSkeleton
		}

		// Set the head position to the be standing up at the origin
		return inputSkeleton.mutate { it[BodyPart.HEAD] = headBone.copy(rawPosition = Vector3(0f, skeletonHeight, 0f)) }
	}
}
