package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

// Source: Plagenhoef et al., 1983 (Table 4)
// Modified for more segmentation
// Should add up to 1
val BODY_PART_MASSES = mapOf(
	BodyPart.HEAD to 0.0827f,
	BodyPart.LEFT_UPPER_ARM to 0.0263f,
	BodyPart.RIGHT_UPPER_ARM to 0.0263f,
	BodyPart.LEFT_LOWER_ARM to 0.0224f,
	BodyPart.RIGHT_LOWER_ARM to 0.0224f,
	BodyPart.UPPER_CHEST to 0.0935f,
	BodyPart.LOWER_CHEST to 0.0935f,
	BodyPart.UPPER_WAIST to 0.0660f,
	BodyPart.LOWER_WAIST to 0.0660f,
	BodyPart.HIP to 0.1525f,
	BodyPart.LEFT_UPPER_LEG to 0.1122f,
	BodyPart.RIGHT_UPPER_LEG to 0.1122f,
	BodyPart.LEFT_LOWER_LEG to 0.0620f,
	BodyPart.RIGHT_LOWER_LEG to 0.0620f,
)

fun centreOfMass(
	bones: ComputedSkeleton,
): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.ZERO) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCentre = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCentre * massEntry.value)
}
