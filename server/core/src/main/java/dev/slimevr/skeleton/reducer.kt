package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
	is SkeletonActions.SetBoneRotation -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = action.rotation, isRotationActive = action.setActive) })
	}

	is SkeletonActions.SetBonePosition -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawPosition = action.position, isPositionActive = action.setActive) })
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = Quaternion.IDENTITY, rawPosition = null, isRotationActive = false, isPositionActive = false) })
	}

	is SkeletonActions.SetProportions -> {
		val bones = action.lengths.toBoneOffsets()
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
			bone.copy(offset = bones[bodyPart] ?: bone.offset)
		}
		state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
	}

	is SkeletonActions.PauseTracking -> state.copy(paused = action.pause, pausedBoneInputs = null)

	is SkeletonActions.SetPausedBoneInputs -> state.copy(pausedBoneInputs = action.pausedBoneInputs)

	is SkeletonActions.ComputeFloorLevel -> {
		val skeletonHeight = state.skeletonHeight
		val headHeight = state.boneInputs[BodyPart.HEAD]?.rawPosition?.y ?: skeletonHeight
		state.copy(floorLevel = headHeight - skeletonHeight)
	}

	is SkeletonActions.ResetHeadPosition -> {
		val boneInputs = state.boneInputs
		val headBone = boneInputs[BodyPart.HEAD] ?: return state
		if (headBone.isPositionActive) return state
		state.copy(boneInputs = boneInputs.mutate { it[BodyPart.HEAD] = headBone.copy(rawPosition = null) })
	}
}
