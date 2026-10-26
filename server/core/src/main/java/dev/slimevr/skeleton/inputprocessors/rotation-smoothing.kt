package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.ResetType

private const val SMOOTHING_MULTIPLIER = 100f
private const val SMOOTH_MIN = 0.46f
private const val SMOOTH_MAX = 0.92f

/**
 * Running average of bone rotations to smooth them out.
 */
class RotationSmoothingInputProcessor(val settings: Settings) :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private val smoothed: BodyPartMap<Quaternion> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		val filteringAmount = config.amount
		if (config.type != FilteringType.SMOOTHING || filteringAmount <= 0f) {
			// Drop stale poses so re-enabling doesn't blend out of an outdated frame
			if (smoothed.isNotEmpty()) smoothed.clear()
			lastProcessTime = timeSource.markNow()
			return
		}

		// Normalize with frame time
		val lastFrameTimeSeconds = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		val smoothingAmount = SMOOTH_MIN + filteringAmount * (SMOOTH_MAX - SMOOTH_MIN)
		val alpha = ((1 - smoothingAmount) * lastFrameTimeSeconds * SMOOTHING_MULTIPLIER).coerceIn(0f, 1f)

		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (!bone.isRotationActive) continue

			val prev = smoothed[bodyPart] ?: bone.rotation
			val rotation = prev.lerpQ(bone.rotation, alpha).unit()
			smoothed[bodyPart] = rotation
			if (rotation != bone.rotation) mutableInputSkeleton[bodyPart] = bone.copy(rotation = rotation)
		}
	}

	override fun reset(resetType: ResetType) {
		lastProcessTime = timeSource.markNow()
		smoothed.clear()
	}
}
