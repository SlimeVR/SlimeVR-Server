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
import kotlin.math.asin

const val TOE_SNAP_RANGE_MULTIPLE = 2f
const val MAX_TOE_SNAP_ANGLE = -0.8f
const val ANKLE_DOWN_MIN = 0.65f
const val ANKLE_DOWN_MAX = 0.85f

fun computeToeSnapRatio(
	ankleLocalPosition: Vector3,
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
	val ratioOfMaxAngleToFloor = abs(footAngleToFloor * MAX_TOE_SNAP_ANGLE).coerceIn(0f, 1f)

	// Ratio of range *to* floor
	val rangeToFloorRatio = (1f - ratioOfRangeFromFloor) * ratioOfMaxAngleToFloor

	// Lessen the ratio with the ankle pointing forward.
	val ankleDownDirection = abs(ankleLocalPosition.unit().y.coerceAtMost(0f))
	val ankleDownRatio = FastMath.remap(ankleDownDirection, ANKLE_DOWN_MIN, ANKLE_DOWN_MAX, 0f, 1f).coerceIn(0f, 1f)

	return rangeToFloorRatio * ankleDownRatio
}

fun snapToes(
	rotation: Quaternion,
	correctionRatio: Float,
): Quaternion {
	val heading = rotation.eulerHeading()
	val maxPitch = Quaternion.rotationAroundXAxis(MAX_TOE_SNAP_ANGLE * correctionRatio)
	// Pitch must be applied first
	val maxCorrection = heading * maxPitch
	return rotation.interpQ(maxCorrection, correctionRatio)
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
						ankle.localTailPosition,
						foot.headPosition.y,
						length,
						floorLevel,
					),
				),
			)
		}
	}
}
