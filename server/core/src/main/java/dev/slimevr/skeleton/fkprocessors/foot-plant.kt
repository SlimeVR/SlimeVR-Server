package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import solarxr_protocol.datatypes.BodyPart

class FootPlantFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		if (!settings.context.state.value.data.skeletonConfig.toggles.footPlant) return inputSkeleton

		// TODO
		return inputSkeleton
	}
}
