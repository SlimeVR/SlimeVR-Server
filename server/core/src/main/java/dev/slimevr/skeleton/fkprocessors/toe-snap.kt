package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.mutate
import dev.slimevr.tracker.eulerHeading
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

val TOE_SNAP_RANGE_MULTIPLE = 2f
val MAX_TOE_SNAP_ANGLE = 0.8f

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
): Quaternion {
	// TODO Do we want to retain roll?
	val heading = eulerHeading(rotation)
	// TODO Not yet tested if this is the right method & math
	val maxPitch = Quaternion.rotationAroundZAxis(MAX_TOE_SNAP_ANGLE * correctionRatio)
	// Pitch must be applied first
	val maxCorrection = maxPitch * heading
	return rotation.interpR(maxCorrection, correctionRatio)
}

class ToeSnapFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	val bodyParts: Array<BodyPart> = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

	override fun process(inputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float): InputSkeleton {
		if (!settings.context.state.value.data.skeletonConfig.toggles.toeSnap) return inputSkeleton

		return inputSkeleton.mutate {
			// TODO This loop format should be turned into a function
			for (bodyPart in bodyParts) {
				val input = it[bodyPart] ?: continue
				val output = fk[bodyPart] ?: continue
				it[bodyPart] = input.copy(
					rawRotation = snapToes(
						input.rawRotation,
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
