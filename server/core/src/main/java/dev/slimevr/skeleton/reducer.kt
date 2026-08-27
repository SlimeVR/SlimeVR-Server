package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
	is SkeletonActions.SetBoneRotation -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = action.rotation, isActive = true) })
	}

	is SkeletonActions.SetBonePosition -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawPosition = action.position, isActive = true) })
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = Quaternion.IDENTITY, rawPosition = Vector3.NULL, isActive = false) })
	}

	is SkeletonActions.SetProportions -> {
		val bones = action.lengths.toBoneOffsets()
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
			bone.copy(offset = bones[bodyPart] ?: bone.offset)
		}
		state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
	}

	is SkeletonActions.PauseTracking -> {
		state.copy(paused = action.pause, pausedBoneInputs = null)
	}

	is SkeletonActions.SetPausedBoneInputs -> {
		state.copy(pausedBoneInputs = action.pausedBoneInputs)
	}
}
