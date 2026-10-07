package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.ResetType

private data class BoneInterpolation(
	val rotation: Quaternion,
	val acceleration: Vector3,
	val position: Vector3?,
)

/**
 * Since the skeleton runs at a different rate than trackers (e.g: 500hz vs 100hz),
 * this processor handles interpolating data received from trackers across skeleton frames up
 * until the next tracker data (e.g: interpolating the 100hz data across 5 frames to reach 500hz).
 *
 * This has the side effect of smoothing data a bit but that is **not** its primary goal as it will run whether
 * smoothing filtering is enabled or not.
 * Its main goal is to make the data more reliable and easier to work with by avoiding spikes over frames as the skeleton
 * receives new data from trackers. We trade off a little bit of latency (~4 ms) for this.
 *
 * With this, processors don't have to care that trackers only send at 100hz and can know that the data from one skeleton
 * frame to another is reliable.
 * For example, since rotation is interpolated here, the velocity processor doesn't have to do its own interpolation.
 */
class UpsamplingInputProcessor :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private var interpolationData: BodyPartMap<BoneInterpolation> = bodyPartMap()
	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val deltaTime = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		mutableInputSkeleton.forEachBone { bodyPart, bone ->
			if (bone.expectedTps == null) return@forEachBone

			// For a 100hz tracker on 500hz skeleton, this will be 0.2f (interpolating 1/5 every frame)
			val interpolationRatio = (deltaTime * bone.expectedTps.toFloat()).coerceAtMost(1f)
			val newBone = interpolationData[bodyPart]?.let { interpolation ->
				// Interpolate rotation, acceleration and position.
				val rotation = if (bone.isRotationActive) {
					interpolation.rotation.interpQ(bone.rotation, interpolationRatio)
				} else {
					bone.rotation
				}
				val acceleration = if (bone.isAccelerationActive) {
					interpolation.acceleration.lerp(bone.acceleration, interpolationRatio)
				} else {
					bone.acceleration
				}
				val position = if (bone.isPositionActive && bone.position != null && interpolation.position != null) {
					interpolation.position.lerp(bone.position, interpolationRatio)
				} else {
					bone.position
				}

				// Return modified bone with interpolated data
				bone.copy(
					rotation = rotation,
					acceleration = acceleration,
					position = position,
				)
			} ?: bone

			// Set new interpolated bone data
			if (bone != newBone) mutableInputSkeleton[bodyPart] = newBone

			// Set running interpolation data
			interpolationData[bodyPart] = BoneInterpolation(
				newBone.rotation,
				newBone.acceleration,
				newBone.position,
			)
		}
	}

	override fun reset(resetType: ResetType) {
		interpolationData.clear()
		lastProcessTime = timeSource.markNow()
	}
}
