package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
	is SkeletonActions.SetBoneRotation -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.bodyPart] = bone.copy(rotation = action.rotation, isRotationActive = action.setActive) })
	}

	is SkeletonActions.SetBoneAcceleration -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.bodyPart] = bone.copy(acceleration = action.acceleration, isAccelerationActive = action.setActive) })
	}

	is SkeletonActions.SetBonePosition -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.bodyPart] = bone.copy(position = action.position ?: Vector3.ZERO, isPositionActive = action.setActive) })
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.bodyPart] = bone.copy(
					rotation = Quaternion.IDENTITY,
					acceleration = Vector3.ZERO,
					position = Vector3.ZERO,
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

	is SkeletonActions.ResetFloorLevel -> {
		val skeletonHeight = state.skeletonHeight
		val headBone = state.boneInputs[BodyPart.HEAD]
		if (headBone != null && headBone.isPositionActive) {
			val headHeight = headBone.position.y
			state.copy(floorLevel = headHeight - skeletonHeight)
		} else {
			state.copy(floorLevel = 0f)
		}
	}

	is SkeletonActions.RequestProcessorReset -> state.copy(processorResets = state.processorResets + action.resetType)
	is SkeletonActions.ProcessorResetsApplied -> state.copy(processorResets = state.processorResets.drop(action.count))
}
