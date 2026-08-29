package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.IKTargets
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.SkeletonTargetProcessor
import dev.slimevr.tracker.eulerHeading
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

// The max height to plant feet by
val ROTATION_CORRECTION_VERTICAL = 0.1f

fun computeFootPlantRatio(
	ankleHeight: Float,
	floorHeight: Float,
	// Normalized distance from ankle to floor within the correction range
): Float = 1f - ((ankleHeight - floorHeight) / ROTATION_CORRECTION_VERTICAL).coerceIn(0f, 1f)

fun correctFootAttitude(
	rotation: Quaternion,
	correctionRatio: Float,
	// eulerHeading is already twinNearest, so we can just use interpQ
): Quaternion = rotation.interpQ(eulerHeading(rotation), correctionRatio)

class FootPlantFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		if (!settings.context.state.value.data.skeletonConfig.toggles.footPlant) return inputSkeleton

		// TODO
		return inputSkeleton
	}
}
