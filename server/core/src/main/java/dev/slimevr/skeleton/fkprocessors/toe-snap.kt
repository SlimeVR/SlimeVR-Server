package dev.slimevr.skeleton.fkprocessors

import com.jme3.math.FastMath
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs

const val TOE_SNAP_RANGE_MULTIPLIER = 2f
const val MAX_TOE_SNAP_RATIO = 0.8f
const val ANKLE_DOWN_MIN = 0.65f
const val ANKLE_DOWN_MAX = 0.85f

fun computeToeSnapRatio(
	ankleHeight: Float,
	footLength: Float,
	floorLevel: Float,
	ankleLocalPosition: Vector3,
): Float {
	val ankleHeightAboveFloor = ankleHeight - floorLevel

	// Compute the floor to floor ratio. 0 = heel is touching floor, 1 = tip of foot is touching floor
	val footToFloorRatio = ankleHeightAboveFloor.coerceIn(0f, footLength) / footLength

	// Compute the range over which the toes can snap to the floor
	val potentialToeHeightAboveFloor = ankleHeightAboveFloor - footLength
	val toeSnapRange = footLength * TOE_SNAP_RANGE_MULTIPLIER
	val ratioOfRangeFromFloor = (potentialToeHeightAboveFloor / toeSnapRange).coerceIn(0f, 1f)

	// Ratio of range *to* floor
	val rangeToFloorRatio = (1f - ratioOfRangeFromFloor) * footToFloorRatio

	// Lessen the ratio as the ankle doesn't point down
	val ankleDownDirection = abs(ankleLocalPosition.unit().y.coerceAtMost(0f))
	val ankleDownRatio = FastMath.remapClamped(ankleDownDirection, ANKLE_DOWN_MIN, ANKLE_DOWN_MAX, 0f, 1f)

	return rangeToFloorRatio * ankleDownRatio
}

private val MAX_TOE_SNAP_PITCH = Quaternion.rotationAroundXAxis(-MAX_TOE_SNAP_RATIO)
fun snapToes(
	rotation: Quaternion,
	correctionRatio: Float,
): Quaternion {
	// Pitch must be applied first
	val maxCorrected = rotation.eulerHeading() * MAX_TOE_SNAP_PITCH
	return rotation.interpQ(maxCorrected, correctionRatio)
}

class ToeSnapFkProcessor(val skeleton: Skeleton) : SkeletonFkProcessor {
	val bodyParts = arrayOf(
		BodyPart.LEFT_LOWER_LEG to BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_LOWER_LEG to BodyPart.RIGHT_FOOT,
	)

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!skeleton.effectiveToeSnap) return

		for ((anklePart, footPart) in bodyParts) {
			val input = mutableInputSkeleton[footPart] ?: continue
			if (input.isRotationActive) continue
			val length = input.offset.len()
			if (length <= 0f) continue
			val ankle = fk[anklePart] ?: continue
			val foot = fk[footPart] ?: continue
			mutableInputSkeleton[footPart] = input.copy(
				rotation = snapToes(
					input.rotation,
					computeToeSnapRatio(
						foot.headPosition.y,
						length,
						floorLevel,
						ankle.localTailPosition,
					),
				),
			)
		}
	}
}
