package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BoneInput
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.util.Side
import dev.slimevr.util.side
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

private const val INTERMEDIATE_FROM_PROXIMAL_CURL = 2.12f
private const val DISTAL_FROM_PROXIMAL_CURL = 3.03f

private const val INTERMEDIATE_FROM_PROXIMAL = 1.04f
private const val DISTAL_FROM_PROXIMAL = 1.10f

private const val REST_ANGLE = 0.15f

private fun getRestRotation(handRotation: Quaternion, axis: Vector3, multiplier: Float): Quaternion {
	val restRotation = Quaternion.fromRotationVector(axis * REST_ANGLE)
	return handRotation * restRotation.pow(multiplier)
}

/**
 * Interpolate the forward, back and side rotation with different ratios: one ratio for forward and one ratio for back and side.
 */
private fun interpolateForwardBackSide(hand: Quaternion, finger: Quaternion, forwardRatio: Float, backSideRatio: Float, axis: Vector3): Quaternion {
	// Work with the local rotation to avoid axes being ambiguous
	val local = finger.localAbout(hand)

	// If forward is going back, actually use the backSideRatio.
	val actualForwardRatio = if (local.angleAboutR(axis) < 0f) {
		backSideRatio
	} else {
		forwardRatio
	}

	// Early return for optimization
	if (actualForwardRatio == backSideRatio) return hand.interpQ(finger, backSideRatio)

	// Decompose the local rotation into its twist (forward) and its swing (side) along the axis
	val forward = local.project(axis).unit()
	val side = local / forward

	// Scale each axis of the local rotation with our ratios
	val scaledForward = forward.pow(actualForwardRatio)
	val scaledSide = side.pow(backSideRatio)

	// Recompose the local rotation. We removed forward from local rotation to get side, so we must add forward to side back.
	val scaledLocal = scaledSide * scaledForward

	// Return scaled local rotation applied to From rotation
	return scaledLocal.globalAbout(hand)
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
		val axis: Vector3,
		val isThumb: Boolean = false,
	)

	private val fingers = arrayOf(
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL, Vector3.NEG_X, true),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_DISTAL, Vector3.POS_Z),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_MIDDLE_PROXIMAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL, Vector3.POS_Z),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_DISTAL, Vector3.POS_Z),
		Finger(BodyPart.LEFT_HAND, BodyPart.LEFT_LITTLE_PROXIMAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL, Vector3.POS_Z),

		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL, Vector3.NEG_X, true),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL, Vector3.NEG_Z),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL, Vector3.NEG_Z),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL, Vector3.NEG_Z),
		Finger(BodyPart.RIGHT_HAND, BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL, Vector3.NEG_Z),
	)

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for (finger in fingers) {
			val handRotation = mutableInputSkeleton[finger.hand]?.rotation ?: continue
			val proximal = mutableInputSkeleton[finger.proximal] ?: continue
			val intermediate = mutableInputSkeleton[finger.intermediate] ?: continue
			val distal = mutableInputSkeleton[finger.distal] ?: continue

			// Proximal
			if (!proximal.isRotationActive) {
				// Missing proximal bone. Impute it from intermediate > distal > hand
				val newRotation = if (intermediate.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						intermediate.rotation,
						1f / INTERMEDIATE_FROM_PROXIMAL_CURL,
						1f / INTERMEDIATE_FROM_PROXIMAL,
						finger.axis,
					)
				} else if (distal.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						distal.rotation,
						1f / DISTAL_FROM_PROXIMAL_CURL,
						1f / DISTAL_FROM_PROXIMAL,
						finger.axis,
					)
				} else {
					getRestRotation(handRotation, finger.axis, 1f)
				}
				mutableInputSkeleton[finger.proximal] = proximal.copy(rotation = newRotation)
			}

			// Intermediate
			if (!intermediate.isRotationActive) {
				// Missing intermediate bone. Impute it from distal > proximal > hand
				val newRotation = if (distal.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						distal.rotation,
						INTERMEDIATE_FROM_PROXIMAL_CURL / DISTAL_FROM_PROXIMAL_CURL,
						INTERMEDIATE_FROM_PROXIMAL / DISTAL_FROM_PROXIMAL,
						finger.axis,
					)
				} else if (proximal.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						proximal.rotation,
						INTERMEDIATE_FROM_PROXIMAL_CURL,
						INTERMEDIATE_FROM_PROXIMAL,
						finger.axis,
					)
				} else {
					getRestRotation(handRotation, finger.axis, INTERMEDIATE_FROM_PROXIMAL_CURL)
				}
				mutableInputSkeleton[finger.intermediate] = intermediate.copy(rotation = newRotation)
			}

			// Distal
			if (!distal.isRotationActive) {
				// Missing distal bone. Impute it from intermediate > proximal > hand
				val newRotation = if (intermediate.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						intermediate.rotation,
						DISTAL_FROM_PROXIMAL_CURL / INTERMEDIATE_FROM_PROXIMAL_CURL,
						DISTAL_FROM_PROXIMAL / INTERMEDIATE_FROM_PROXIMAL,
						finger.axis,
					)
				} else if (proximal.isRotationActive) {
					interpolateForwardBackSide(
						handRotation,
						proximal.rotation,
						DISTAL_FROM_PROXIMAL_CURL,
						DISTAL_FROM_PROXIMAL,
						finger.axis,
					)
				} else {
					getRestRotation(handRotation, finger.axis, DISTAL_FROM_PROXIMAL_CURL)
				}
				mutableInputSkeleton[finger.distal] = distal.copy(rotation = newRotation)
			}
		}
	}
}
