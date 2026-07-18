package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType

interface SkeletonProcessor {
	fun process(state: SkeletonState): SkeletonState
}

/**
 * Handles replacing rotations of rawBones that are not actively receiving data by falling back
 * to either another rawBone's rotation, their parent's yaw or the identity rotation.
 */
class FallbackProcessor : SkeletonProcessor {
	private val leftFingerChains = listOf(
		listOf(BodyPart.LEFT_THUMB_DISTAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_METACARPAL),
		listOf(BodyPart.LEFT_INDEX_DISTAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_PROXIMAL),
		listOf(BodyPart.LEFT_MIDDLE_DISTAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_PROXIMAL),
		listOf(BodyPart.LEFT_RING_DISTAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_PROXIMAL),
		listOf(BodyPart.LEFT_LITTLE_DISTAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_PROXIMAL),
	)
	private val rightFingerChains = listOf(
		listOf(BodyPart.RIGHT_THUMB_DISTAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_METACARPAL),
		listOf(BodyPart.RIGHT_INDEX_DISTAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_PROXIMAL),
		listOf(BodyPart.RIGHT_MIDDLE_DISTAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_PROXIMAL),
		listOf(BodyPart.RIGHT_RING_DISTAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_PROXIMAL),
		listOf(BodyPart.RIGHT_LITTLE_DISTAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_PROXIMAL),
	)

	private fun putFingerFallbacks(
		map: MutableMap<BodyPart, List<BodyPart>>,
		chain: List<BodyPart>,
		hand: BodyPart,
	) {
		chain.forEachIndexed { i, segment ->
			map[segment] = chain.drop(i + 1) + hand
		}
	}

	/**
	 * First element is the BodyPart whose rawBone is not actively receiving data.
	 * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
	 */
	private val missingToFallbacks = mapOf(
		BodyPart.HEAD to setOf(BodyPart.NECK),
		BodyPart.NECK to setOf(BodyPart.HEAD),

		// TODO could be removed once ImputeSpineProcessor is done vvv
		BodyPart.UPPER_CHEST to setOf(BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.WAIST to setOf(BodyPart.CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.HIP to setOf(BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),

		BodyPart.LEFT_HIP to setOf(BodyPart.HIP, BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
		BodyPart.RIGHT_HIP to setOf(BodyPart.HIP, BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),

		BodyPart.LEFT_SHOULDER to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.RIGHT_SHOULDER to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),

		BodyPart.LEFT_UPPER_ARM to setOf(BodyPart.LEFT_LOWER_ARM),
		BodyPart.RIGHT_UPPER_ARM to setOf(BodyPart.RIGHT_LOWER_ARM),
		BodyPart.LEFT_LOWER_ARM to setOf(BodyPart.LEFT_UPPER_ARM),
		BodyPart.RIGHT_LOWER_ARM to setOf(BodyPart.RIGHT_UPPER_ARM),
		BodyPart.LEFT_HAND to setOf(BodyPart.LEFT_LOWER_ARM, BodyPart.LEFT_UPPER_ARM),
		BodyPart.RIGHT_HAND to setOf(BodyPart.RIGHT_LOWER_ARM, BodyPart.RIGHT_UPPER_ARM),

		// TODO could be removed once ImputeFingersProcessor is done vvv
		BodyPart.LEFT_THUMB_METACARPAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_THUMB_PROXIMAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_THUMB_DISTAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_INDEX_PROXIMAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_INDEX_INTERMEDIATE to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_INDEX_DISTAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_MIDDLE_PROXIMAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_MIDDLE_DISTAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_RING_PROXIMAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_RING_INTERMEDIATE to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_RING_DISTAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_LITTLE_PROXIMAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_LITTLE_INTERMEDIATE to setOf(BodyPart.LEFT_HAND),
		BodyPart.LEFT_LITTLE_DISTAL to setOf(BodyPart.LEFT_HAND),
		BodyPart.RIGHT_THUMB_METACARPAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_THUMB_PROXIMAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_THUMB_DISTAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_INDEX_PROXIMAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_INDEX_INTERMEDIATE to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_INDEX_DISTAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_MIDDLE_PROXIMAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_MIDDLE_DISTAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_RING_PROXIMAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_RING_INTERMEDIATE to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_RING_DISTAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_LITTLE_PROXIMAL to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to setOf(BodyPart.RIGHT_HAND),
		BodyPart.RIGHT_LITTLE_DISTAL to setOf(BodyPart.RIGHT_HAND),
	)

	override fun process(state: SkeletonState): SkeletonState {
		val rawBones = state.rawBones

		return state.copy(
			rawBones = rawBones.mapValues { (bodyPart, bone) ->
				if (bone.isActive) return@mapValues bone

				bone.copy(rawRotation = computeFallbackRotation(rawBones, bodyPart))
			},
		)
	}

	private fun computeFallbackRotation(rawBones: Map<BodyPart, RawBone>, bodyPart: BodyPart): Quaternion {
		// Fallback bone's rotation
		missingToFallbacks[bodyPart]
			?.firstNotNullOfOrNull { part ->
				rawBones[part]?.takeIf { it.isActive }
			}
			?.let { return it.rawRotation }

		// First active parent bone's yaw
		rawBones[
			findBodyPartParent(bodyPart) { part ->
				rawBones[part]?.isActive == true
			},
		]?.let { return it.rawRotation.project(Vector3.POS_Y).unit() }

		// Fallback to identity
		return Quaternion.IDENTITY
	}
}

/**
 * Handles imputing the rotation of spine bones that are not actively receiving data from the rotations
 * of nearby bones.
 *
 * Similar to FallbackProcessor but specialized for the spine, using more data and math for better results.
 */
class ImputeSpineProcessor(val settings: Settings) : SkeletonProcessor {
	override fun process(state: SkeletonState): SkeletonState = state
}

class SmoothingProcessor(val settings: Settings) : SkeletonProcessor {
	private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()
	private var smoothedLengths: Map<BodyPart, Vector3> = emptyMap()

	// TODO this isn't linear. Do we want linear smoothing like in main?
	override fun process(state: SkeletonState): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.SMOOTHING) return state

		val alpha = 1 - (SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN))

		smoothedRotations = state.rawBones.mapValues { (bodyPart, bone) ->
			(smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
		}
		smoothedLengths = state.rawBones.mapValues { (bodyPart, bone) ->
			val prev = smoothedLengths[bodyPart] ?: bone.offset
			prev + (bone.offset - prev) * alpha
		}
		return state.copy(
			rawBones = state.rawBones.mapValues { (bodyPart, bone) ->
				bone.copy(
					rawRotation = smoothedRotations[bodyPart] ?: bone.rawRotation,
					offset = smoothedLengths[bodyPart] ?: bone.offset,
				)
			},
		)
	}

	companion object {
		private const val SMOOTH_MIN = 0.63f
		private const val SMOOTH_MAX = 0.94f
	}
}

class PredictionProcessor(val settings: Settings) : SkeletonProcessor {
	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastOffset: Vector3,
		val offsetDelta: Vector3,
	)

	private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.PREDICTION) return state

		val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()
		val newBones = state.rawBones.mapValues { (bodyPart, bone) ->
			val prev = velocities[bodyPart]
			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, Quaternion.IDENTITY, bone.offset, Vector3.NULL)
				return@mapValues bone
			}
			val rotationDelta = if (bone.rawRotation !== prev.lastRotation) {
				bone.rawRotation * prev.lastRotation.inv()
			} else {
				prev.rotationDelta
			}
			val lengthDelta = if (bone.offset != prev.lastOffset) {
				bone.offset - prev.lastOffset
			} else {
				prev.offsetDelta
			}
			newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, rotationDelta, bone.offset, lengthDelta)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, config.amount).unit()
			bone.copy(
				rawRotation = (scaledDelta * bone.rawRotation).unit(),
				offset = bone.offset + lengthDelta * config.amount,
			)
		}
		velocities = newVelocities
		return state.copy(rawBones = newBones)
	}
}
