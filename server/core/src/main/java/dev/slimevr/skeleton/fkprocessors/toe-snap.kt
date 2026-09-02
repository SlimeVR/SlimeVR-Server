package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.mutateCopy
import dev.slimevr.tracker.eulerHeading
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs
import kotlin.math.asin

const val TOE_SNAP_RANGE_MULTIPLE = 2f
const val MAX_TOE_SNAP_ANGLE = -0.8f

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

	// Reduce the range back down as we touch the floor
	val footAngleToFloor = asin(ankleHeightAboveFloor.coerceIn(0f, footLength) / footLength)
	val ratioOfMaxAngleToFloor = abs(footAngleToFloor / MAX_TOE_SNAP_ANGLE).coerceIn(0f, 1f)

	// Ratio of range *to* floor
	return (1f - ratioOfRangeFromFloor) * ratioOfMaxAngleToFloor
}

fun snapToes(
	rotation: Quaternion,
	correctionRatio: Float,
): Quaternion {
	// TODO Do we want to retain roll?
	val heading = eulerHeading(rotation)
	// TODO Not yet tested if this is the right method & math
	val maxPitch = Quaternion.rotationAroundXAxis(MAX_TOE_SNAP_ANGLE * correctionRatio)
	// Pitch must be applied first
	val maxCorrection = heading * maxPitch
	return rotation.interpQ(maxCorrection, correctionRatio)
}

class ToeSnapFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		if (!settings.context.state.value.data.skeletonConfig.toggles.toeSnap) return inputSkeleton

		return inputSkeleton.mutateCopy {
			// TODO This loop format should be turned into a function
			for (bodyPart in bodyParts) {
				val input = it[bodyPart] ?: continue
				val output = fk[bodyPart] ?: continue
				it[bodyPart] = input.copy(
					rotation = snapToes(
						input.rotation,
						computeToeSnapRatio(
							output.headPosition.y,
							input.offset.len(),
							floorLevel,
						),
					),
				)
			}
		}
	}
}
