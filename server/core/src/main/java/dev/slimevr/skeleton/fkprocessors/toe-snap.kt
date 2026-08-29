package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.tracker.eulerHeading
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

val TOE_SNAP_RANGE_MULTIPLE = 2f

fun computeToeSnapRatio(
	ankleHeight: Float,
	footLength: Float,
	floorHeight: Float,
): Float {
	val ankleHeightAboveFloor = ankleHeight - floorHeight
	// Toe height if the foot is pointing directly down to the floor
	val potentialToeHeightAboveFloor = ankleHeightAboveFloor - footLength
	// The range over which the toes snap to the floor
	val toeSnapRange = footLength * TOE_SNAP_RANGE_MULTIPLE
	val ratioOfRangeFromFloor = (potentialToeHeightAboveFloor / toeSnapRange).coerceIn(0f, 1f)
	// Ratio of range *to* floor
	return 1f - ratioOfRangeFromFloor
}

fun snapToes(
	rotation: Quaternion,
	correctionRatio: Float,
	// eulerHeading is already twinNearest, so we can just use interpQ
): Quaternion = rotation.interpQ(eulerHeading(rotation), correctionRatio)

class ToeSnapFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		if (!settings.context.state.value.data.skeletonConfig.toggles.toeSnap) return inputSkeleton

		// TODO
		return inputSkeleton
	}
}
