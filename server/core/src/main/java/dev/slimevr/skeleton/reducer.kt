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
		state.copy(boneInputs = state.boneInputs.mutateCopy { it[action.bodyPart] = bone.copy(position = action.position, isPositionActive = action.setActive) })
	}

	is SkeletonActions.DisableBone -> {
		val bone = state.boneInputs[action.bodyPart] ?: return state
		state.copy(
			boneInputs = state.boneInputs.mutateCopy {
				it[action.bodyPart] = bone.copy(
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
		val bones = action.lengths.toBoneOffsets()
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
			bone.copy(offset = bones[bodyPart] ?: bone.offset)
		}
		state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
	}

	is SkeletonActions.PauseTracking -> state.copy(paused = action.pause, pausedProcessedBoneInputs = null)

	is SkeletonActions.SetPausedBoneInputs -> state.copy(pausedProcessedBoneInputs = action.pausedBoneInputs)

	is SkeletonActions.ResetHeadPosition -> {
		val boneInputs = state.boneInputs
		val headBone = boneInputs[BodyPart.HEAD] ?: return state
		if (headBone.isPositionActive) return state
		state.copy(boneInputs = boneInputs.mutateCopy { it[BodyPart.HEAD] = headBone.copy(position = null) })
	}

	is SkeletonActions.ComputeFloorLevel -> {
		val skeletonHeight = state.skeletonHeight
		val headHeight = state.boneInputs[BodyPart.HEAD]?.position?.y ?: skeletonHeight
		state.copy(floorLevel = headHeight - skeletonHeight)
	}
}
