package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

private val FINGER_OFFSETS = (
	iterateBodyPartHierarchy(BodyPart.LEFT_HAND, true) +
		iterateBodyPartHierarchy(BodyPart.RIGHT_HAND, true)
	).map { it.second }.associateWith { Vector3(0f, -0.025f, 0f) }

val BONE_TAIL_OFFSETS: Map<BodyPart, Vector3> = DEFAULT_PROPORTIONS.toBodyPartOffsets() + FINGER_OFFSETS

val BONE_TAIL_DIRECTIONS: Map<BodyPart, Vector3> = BONE_TAIL_OFFSETS.mapValues { (_, vec) -> vec.unit() }
