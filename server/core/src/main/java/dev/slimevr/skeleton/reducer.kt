package dev.slimevr.skeleton

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.boneId
import dev.slimevr.bones.mapValues
import dev.slimevr.bones.mutateCopy
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
	is SkeletonActions.SetBoneRotation -> {
		val bone = state.boneInputs[action.boneId] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.boneId] = bone.copy(rotation = action.rotation, isRotationActive = action.setActive) })
	}

	is SkeletonActions.SetBoneAcceleration -> {
		val bone = state.boneInputs[action.boneId] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.boneId] = bone.copy(acceleration = action.acceleration, isAccelerationActive = action.setActive) })
	}

	is SkeletonActions.SetBonePosition -> {
		val bone = state.boneInputs[action.boneId] ?: return state
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.boneId] = bone.copy(position = action.position, isPositionActive = action.setActive) })
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.boneId] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.boneId] = bone.copy(
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
		val newBones = state.boneInputs.mapValues { boneId, bone ->
			bone.copy(
				headOffset = action.boneOffsets.head[boneId] ?: bone.headOffset,
				offset = action.boneOffsets.tail[boneId] ?: bone.offset,
			)
		}
		state.copy(boneInputs = newBones, skeletonHeight = action.skeletonHeight, proportionValues = action.values)
	}

	is SkeletonActions.PauseTracking -> state.copy(paused = action.pause, pausedProcessedBoneInputs = null)

	is SkeletonActions.SetPausedBoneInputs -> state.copy(pausedProcessedBoneInputs = action.pausedBoneInputs)

	is SkeletonActions.ResetHeadPosition -> {
		val boneInputs = state.boneInputs
		val headBone = boneInputs[BodyPart.HEAD.boneId] ?: return state
		if (headBone.isPositionActive) return state
		state.copy(boneInputs = boneInputs.mutateCopy { it[BodyPart.HEAD.boneId] = headBone.copy(position = null) })
	}

	is SkeletonActions.ComputeFloorLevel -> {
		val skeletonHeight = state.skeletonHeight
		val headHeight = state.boneInputs[BodyPart.HEAD.boneId]?.position?.y ?: skeletonHeight
		state.copy(floorLevel = headHeight - skeletonHeight)
	}
}
