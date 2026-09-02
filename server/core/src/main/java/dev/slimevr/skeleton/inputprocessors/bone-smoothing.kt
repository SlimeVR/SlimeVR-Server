package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.rpc.FilteringType

/**
 * Running average of bone rotations to smooth them out.
 */
class BoneSmoothingInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	private var smoothed: BodyPartMap<Quaternion> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	// TODO this isn't linear. Do we want linear smoothing like in main?
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.SMOOTHING) {
			// Drop stale poses so re-enabling doesn't blend out of an outdated frame
			if (smoothed.isNotEmpty()) smoothed.clear()
			lastProcessTime = timeSource.markNow()
			return
		}

		// Normalize with frame time
		val lastFrameTimeSeconds = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		val smoothingAmount = SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN)
		val alpha = ((1 - smoothingAmount) * lastFrameTimeSeconds * SMOOTHING_MULTIPLIER).coerceIn(0f, 1f)

		val newSmoothed = bodyPartMap<Quaternion>()
		inputSkeleton.forEachBone { bodyPart, bone ->
			val prev = smoothed[bodyPart] ?: bone.rotation
			val rotation = prev.lerpR(bone.rotation, alpha).unit()
			newSmoothed[bodyPart] = rotation
			if (rotation != bone.rotation) inputSkeleton[bodyPart] = bone.copy(rotation = rotation)
		}
		smoothed = newSmoothed
	}

	companion object {
		private const val SMOOTHING_MULTIPLIER = 100f
		private const val SMOOTH_MIN = 0.6f
		private const val SMOOTH_MAX = 0.95f
	}
}
