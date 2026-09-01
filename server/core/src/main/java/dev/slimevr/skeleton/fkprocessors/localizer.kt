package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.mutateCopy
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

class LocalizerFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		val headInput = inputSkeleton[BodyPart.HEAD] ?: return inputSkeleton
		if (headInput.isPositionActive || !settings.context.state.value.data.skeletonConfig.toggles.mocapMode) {
			return inputSkeleton
		}

		// TODO logic for this
		return inputSkeleton.mutateCopy { updated -> updated[BodyPart.HEAD] = headInput.copy(rawPosition = headInput.rawPosition?.let { Vector3(it.x, it.y, it.z) }) }
	}
}
