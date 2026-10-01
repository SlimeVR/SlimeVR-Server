package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BoneInput
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.util.Side
import dev.slimevr.util.side
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

private const val INTERMEDIATE_FROM_PROXIMAL = 2.12f
private const val DISTAL_FROM_PROXIMAL = 3.03f

private val PROXIMAL_REST_ROTATION = Quaternion.rotationAroundZAxis(0.2f)
private val PROXIMAL_THUMB_REST_ROTATION = Quaternion.rotationAroundXAxis(-0.15f)
private val THUMB_PARTS = arrayOf(
	BodyPart.LEFT_THUMB_METACARPAL,
	BodyPart.LEFT_THUMB_PROXIMAL,
	BodyPart.LEFT_THUMB_DISTAL,
	BodyPart.RIGHT_THUMB_METACARPAL,
	BodyPart.RIGHT_THUMB_PROXIMAL,
	BodyPart.RIGHT_THUMB_DISTAL,
)

private fun getRestRotation(hand: BoneInput, finger: BoneInput, multiplier: Float): Quaternion {
	val isThumb = finger.bodyPart in THUMB_PARTS
	val restRotation = if (isThumb) {
		PROXIMAL_THUMB_REST_ROTATION
	} else {
		val side = hand.bodyPart.side
		if (side == Side.RIGHT) {
			PROXIMAL_REST_ROTATION.inv()
		} else {
			PROXIMAL_REST_ROTATION
		}
	}
	return hand.rotation * restRotation.pow(multiplier)
}

/**
 * Handles rotations of inactive finger bones.
 */
class FingersInputProcessor : SkeletonInputProcessor {
	private data class Finger(
		val hand: BodyPart,
		val proximal: BodyPart,
		val intermediate: BodyPart,
		val distal: BodyPart,
	)

	private val fingers = arrayOf(
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_DISTAL),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_MIDDLE_PROXIMAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_DISTAL),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_LITTLE_PROXIMAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL),

		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL),
	)

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for (finger in fingers) {
			val hand = mutableInputSkeleton[finger.hand] ?: continue
			val proximal = mutableInputSkeleton[finger.proximal] ?: continue
			val intermediate = mutableInputSkeleton[finger.intermediate] ?: continue
			val distal = mutableInputSkeleton[finger.distal] ?: continue

			if (!proximal.isRotationActive) {
				// Missing proximal bone. Impute it from intermediate > distal > hand
				val newRotation = if (intermediate.isRotationActive) {
					hand.rotation.interpQ(intermediate.rotation, 1f / INTERMEDIATE_FROM_PROXIMAL)
				} else if (distal.isRotationActive) {
					hand.rotation.interpQ(distal.rotation, 1f / DISTAL_FROM_PROXIMAL)
				} else {
					getRestRotation(hand, proximal, 1f)
				}
				mutableInputSkeleton[finger.proximal] = proximal.copy(rotation = newRotation)
			}
			if (!intermediate.isRotationActive) {
				// Missing intermediate bone. Impute it from distal > proximal > hand
				val newRotation = if (distal.isRotationActive) {
					hand.rotation.interpQ(distal.rotation, INTERMEDIATE_FROM_PROXIMAL / DISTAL_FROM_PROXIMAL)
				} else if (proximal.isRotationActive) {
					hand.rotation.interpQ(proximal.rotation, INTERMEDIATE_FROM_PROXIMAL)
				} else {
					getRestRotation(hand, proximal, INTERMEDIATE_FROM_PROXIMAL)
				}
				mutableInputSkeleton[finger.intermediate] = intermediate.copy(rotation = newRotation)
			}
			if (!distal.isRotationActive) {
				// Missing distal bone. Impute it from intermediate > proximal > hand
				val newRotation = if (intermediate.isRotationActive) {
					hand.rotation.interpQ(intermediate.rotation, DISTAL_FROM_PROXIMAL / INTERMEDIATE_FROM_PROXIMAL)
				} else if (proximal.isRotationActive) {
					hand.rotation.interpQ(proximal.rotation, DISTAL_FROM_PROXIMAL)
				} else {
					getRestRotation(hand, proximal, DISTAL_FROM_PROXIMAL)
				}
				mutableInputSkeleton[finger.distal] = distal.copy(rotation = newRotation)
			}
		}
	}
}
