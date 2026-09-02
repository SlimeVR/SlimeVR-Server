package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.tracker.eulerHeading
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

// The max height to plant feet by
const val ROTATION_CORRECTION_VERTICAL = 0.1f

fun computeFootPlantRatio(
	ankleHeight: Float,
	floorHeight: Float,
): Float {
	val ankleHeightAboveFloor = ankleHeight - floorHeight
	val ratioOfRangeFromFloor = (ankleHeightAboveFloor / ROTATION_CORRECTION_VERTICAL).coerceIn(0f, 1f)
	// Ratio of range *to* floor
	return 1f - ratioOfRangeFromFloor
}

fun correctFootAttitude(
	rotation: Quaternion,
	correctionRatio: Float,
	// eulerHeading is already twinNearest, so we can just use interpQ
): Quaternion = rotation.interpQ(eulerHeading(rotation), correctionRatio)

class FootPlantFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.footPlant) return

		for (bodyPart in bodyParts) {
			val input = inputSkeleton[bodyPart] ?: continue
			val output = fk[bodyPart] ?: continue
			inputSkeleton[bodyPart] = input.copy(
				rotation = correctFootAttitude(
					input.rotation,
					computeFootPlantRatio(
						output.headPosition.y,
						floorLevel,
					),
				),
			)
		}
	}
}
