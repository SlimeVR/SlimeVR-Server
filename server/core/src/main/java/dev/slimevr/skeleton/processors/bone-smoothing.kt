package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType

/**
 * Running average of bones to smooth them out.
 */
class BoneSmoothingProcessor(val settings: Settings) : SkeletonProcessor {
	private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()
	private var smoothedLengths: Map<BodyPart, Vector3> = emptyMap()

	// TODO this isn't linear. Do we want linear smoothing like in main?
	override fun process(state: SkeletonState): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.SMOOTHING) return state

		val alpha = 1 - (SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN))

		smoothedRotations = state.boneInputs.mapValues { (bodyPart, bone) ->
			(smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
		}
		smoothedLengths = state.boneInputs.mapValues { (bodyPart, bone) ->
			val prev = smoothedLengths[bodyPart] ?: bone.offset
			prev + (bone.offset - prev) * alpha
		}
		return state.copy(
			boneInputs = state.boneInputs.mapValues { (bodyPart, bone) ->
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
