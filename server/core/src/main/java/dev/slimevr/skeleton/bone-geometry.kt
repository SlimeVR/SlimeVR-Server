package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

// TODO Make fingers not all the same lengths https://github.com/SlimeVR/SlimeVR-Server/pull/1781
private val FINGER_OFFSETS = (
	iterateBodyPartHierarchy(BodyPart.LEFT_HAND, true) +
		iterateBodyPartHierarchy(BodyPart.RIGHT_HAND, true)
	).map { it.second }.associateWith { Vector3(0f, -0.025f, 0f) }

private val TOE_OFFSETS = (
	iterateBodyPartHierarchy(BodyPart.LEFT_FOOT, true) +
		iterateBodyPartHierarchy(BodyPart.RIGHT_FOOT, true)
	).map { it.second }.associateWith { Vector3(0f, 0f, -0.025f) }

val DEFAULT_BONE_OFFSETS: BodyPartMap<Vector3> = DEFAULT_PROPORTIONS.toBoneOffsets()
	.mutate { offsets -> for ((bodyPart, offset) in FINGER_OFFSETS + TOE_OFFSETS) offsets[bodyPart] = offset }
