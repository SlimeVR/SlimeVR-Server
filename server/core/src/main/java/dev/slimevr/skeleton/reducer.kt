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
					expectedTps = bone.expectedTps,
					rotation = action.rotation,
					isRotationActive = true,
					acceleration = action.acceleration,
					isAccelerationActive = true,
					position = action.position,
					isPositionActive = action.position != null,
				)
			},
		)
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.bodyPart] = bone.copy(
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

	is SkeletonActions.SetHeadPosition -> {
		val headBone = state.boneInputs[BodyPart.HEAD] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[BodyPart.HEAD] = headBone.copy(position = action.position) })
	}

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
