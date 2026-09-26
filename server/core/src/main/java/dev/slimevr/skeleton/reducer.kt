package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
	is SkeletonActions.SetBonePose -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.bodyPart] = bone.copy(
					trackerOffset = action.trackerOffset ?: bone.trackerOffset,
					expectedTps = action.expectedTps ?: bone.expectedTps,
					rotation = action.rotation ?: bone.rotation,
					acceleration = action.acceleration ?: bone.acceleration,
					position = action.position ?: bone.position,
					isRotationActive = if (action.switchActive) action.rotation != null else bone.isRotationActive,
					isAccelerationActive = if (action.switchActive) action.acceleration != null else bone.isAccelerationActive,
					isPositionActive = if (action.switchActive) action.position != null else bone.isPositionActive,
				)
			},
		)
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.bodyPart] = bone.copy(
					trackerOffset = Vector3.ZERO,
					expectedTps = null,
					rotation = Quaternion.IDENTITY,
					acceleration = Vector3.ZERO,
					position = null,
					isRotationActive = false,
					isAccelerationActive = false,
					isPositionActive = false,
				)
			},
		)
	}

	is SkeletonActions.SetProportions -> {
		val offsets = toBoneOffsets(action.lengths)
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
			bone.copy(
				headOffset = offsets.head[bodyPart] ?: bone.headOffset,
				offset = offsets.tail[bodyPart] ?: bone.offset,
			)
		}
		state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
	}

	is SkeletonActions.PauseTracking -> state.copy(paused = action.pause, pausedProcessedBoneInputs = null)

	is SkeletonActions.SetPausedBoneInputs -> state.copy(pausedProcessedBoneInputs = action.pausedBoneInputs)

	is SkeletonActions.ResetHeadPosition -> {
		val headBone = state.boneInputs[BodyPart.HEAD] ?: return state
		if (headBone.isPositionActive) return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[BodyPart.HEAD] = headBone.copy(position = null) })
	}

	is SkeletonActions.ResetFloorLevel -> {
		val skeletonHeight = state.skeletonHeight
		val headHeight = state.boneInputs[BodyPart.HEAD]?.position?.y ?: skeletonHeight
		state.copy(floorLevel = headHeight - skeletonHeight)
	}

	is SkeletonActions.RequestProcessorReset -> state.copy(processorResets = state.processorResets + action.resetType)

	is SkeletonActions.ProcessorResetsApplied -> state.copy(processorResets = state.processorResets.drop(action.count))

	is SkeletonActions.UpdateLegTweaksTmpOverride -> state.copy(legTweaksTmpOverride = action.transform(state.legTweaksTmpOverride))
}
