package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.FilteringType

/**
 * Running average of bones to smooth them out.
 */
// TODO checking skeletonRefreshRate is not good enough
//  since we're never gonna get that in practice. Need something more robust.
class BoneSmoothingProcessor(val settings: Settings, val skeletonRefreshRate: Int) : SkeletonProcessor {
	private var smoothedRotations: BodyPartMap<Quaternion> = bodyPartMap()
	private var smoothedLengths: BodyPartMap<Vector3> = bodyPartMap()

	// TODO this isn't linear. Do we want linear smoothing like in main?
	override fun process(state: SkeletonState): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.SMOOTHING) return state

		val smoothingAmount = SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN)
		val alpha = (1 - smoothingAmount) / (skeletonRefreshRate / 100f)

		smoothedRotations = state.boneInputs.mapValues { bodyPart, bone ->
			(smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
		}
		smoothedLengths = state.boneInputs.mapValues { bodyPart, bone ->
			val prev = smoothedLengths[bodyPart] ?: bone.offset
			prev + (bone.offset - prev) * alpha
		}
		return state.copy(
			boneInputs = state.boneInputs.mapValues { bodyPart, bone ->
				bone.copy(
					rawRotation = smoothedRotations[bodyPart] ?: bone.rawRotation,
					offset = smoothedLengths[bodyPart] ?: bone.offset,
				)
			},
		)
	}

	companion object {
		private const val SMOOTH_MIN = 0.62f
		private const val SMOOTH_MAX = 0.9f
	}
}
