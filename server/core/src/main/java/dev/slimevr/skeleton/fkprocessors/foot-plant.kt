package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.boneId
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import io.github.axisangles.ktmath.Quaternion

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
): Quaternion = rotation.interpQ(rotation.eulerHeading(), correctionRatio)

class FootPlantFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	private val boneIds: Array<BoneId> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT).map { it.boneId }.toTypedArray()

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.footPlant) return

		for (boneId in boneIds) {
			val input = mutableInputSkeleton[boneId] ?: continue
			if (input.isRotationActive) continue
			val output = fk[boneId] ?: continue
			mutableInputSkeleton[boneId] = input.copy(
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
