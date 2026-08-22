package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.FilteringType

/**
 * Running average of bones to smooth them out.
 */
class BoneSmoothingProcessor(val settings: Settings) : SkeletonProcessor {
	private data class SmoothedBone(val rotation: Quaternion, val offset: Vector3)

	private var smoothed: BodyPartMap<SmoothedBone> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	// TODO this isn't linear. Do we want linear smoothing like in main?
	override fun process(state: SkeletonState): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.SMOOTHING) {
			// Drop stale poses so re-enabling doesn't blend out of an outdated frame
			if (smoothed.isNotEmpty()) smoothed.clear()
			lastProcessTime = timeSource.markNow()
			return state
		}

		// Normalize with frame time
		val lastFrameTimeSeconds = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		val smoothingAmount = SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN)
		val alpha = ((1 - smoothingAmount) * lastFrameTimeSeconds * SMOOTHING_MULTIPLIER).coerceIn(0f, 1f)

		val newSmoothed = bodyPartMap<SmoothedBone>()
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
			val prev = smoothed[bodyPart] ?: SmoothedBone(bone.rawRotation, bone.offset)
			val rotation = prev.rotation.lerpR(bone.rawRotation, alpha).unit()
			val offset = prev.offset + (bone.offset - prev.offset) * alpha
			newSmoothed[bodyPart] = SmoothedBone(rotation, offset)
			bone.copy(rawRotation = rotation, offset = offset)
		}
		smoothed = newSmoothed
		return state.copy(boneInputs = newBones)
	}

	companion object {
		private const val SMOOTHING_MULTIPLIER = 100f
		private const val SMOOTH_MIN = 0.6f
		private const val SMOOTH_MAX = 0.95f
	}
}
