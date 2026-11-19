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

// The max height to plant feet by
private const val ROTATION_CORRECTION_VERTICAL = 0.1f
private const val ANKLE_DOWN_MIN = 0.35f
private const val ANKLE_DOWN_MAX = 0.63f

fun computeFootPlantRatio(
	ankleHeight: Float,
	floorLevel: Float,
	ankleLocalPosition: Vector3,
): Float {
	val ankleHeightAboveFloor = ankleHeight - floorLevel
	val ratioOfRangeFromFloor = (ankleHeightAboveFloor / ROTATION_CORRECTION_VERTICAL).coerceIn(0f, 1f)

	// Ratio of range *to* floor
	val rangeToFloorRatio = 1f - ratioOfRangeFromFloor

	// Lessen the ratio as the ankle doesn't point down
	val ankleDownDirection = abs(ankleLocalPosition.unit().y.coerceAtMost(0f))
	val ankleDownRatio = FastMath.remapClamped(ankleDownDirection, ANKLE_DOWN_MIN, ANKLE_DOWN_MAX, 0f, 1f)

	return rangeToFloorRatio * ankleDownRatio
}

fun correctFootAttitude(
	rotation: Quaternion,
	correctionRatio: Float,
	// eulerHeading is already twinNearest, so we can just use interpQ
): Quaternion = rotation.interpQ(rotation.eulerHeading(), correctionRatio)

class FootPlantFkProcessor(val skeleton: Skeleton) : SkeletonFkProcessor {
	val bodyParts = arrayOf(
		BodyPart.LEFT_LOWER_LEG to BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_LOWER_LEG to BodyPart.RIGHT_FOOT,
	)
	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!skeleton.effectiveFootPlant) return

		for ((anklePart, footPart) in bodyParts) {
			val input = mutableInputSkeleton[footPart] ?: continue
			if (input.isRotationActive) continue

			val foot = fk[footPart] ?: continue
			val ankle = fk[anklePart] ?: continue
			mutableInputSkeleton[footPart] = input.copy(
				rotation = correctFootAttitude(
					input.rotation,
					computeFootPlantRatio(
						foot.headPosition.y,
						floorLevel,
						ankle.localTailPosition,
					),
				),
			)
		}
	}
}
