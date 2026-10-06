package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.skeleton.ComputedSkeleton
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

private val trackerIdsByBodyPart = mapOf(
	BodyPart.HIP to 1,
	BodyPart.LEFT_FOOT to 2,
	BodyPart.RIGHT_FOOT to 3,
	BodyPart.LEFT_UPPER_LEG to 4,
	BodyPart.RIGHT_UPPER_LEG to 5,
	BodyPart.UPPER_CHEST to 6,
	BodyPart.LEFT_UPPER_ARM to 7,
	BodyPart.RIGHT_UPPER_ARM to 8,
)

/** Bones VRChat OSC can accept. Used by the routing module. */
val VRC_OSC_SUPPORTED_BONES: Set<BodyPart> = trackerIdsByBodyPart.keys + ResetBodyParts.TOES

internal fun buildOutgoingBundle(
	bones: ComputedSkeleton,
	routedBones: Set<BodyPart>,
): OscBundle? {
	val messages = buildList {
		for ((bodyPart, trackerId) in trackerIdsByBodyPart) {
			if (bodyPart !in routedBones) continue

			val bone = bones[bodyPart] ?: continue
			add(
				OscContent.Message(
					positionMessage("/tracking/trackers/$trackerId/position", bone.tailPosition),
				),
			)
			add(
				OscContent.Message(
					rotationMessage("/tracking/trackers/$trackerId/rotation", bone.rotation),
				),
			)
		}

		bones[BodyPart.HEAD]?.let { head ->
			add(
				OscContent.Message(
					positionMessage("/tracking/trackers/head/position", head.tailPosition),
				),
			)
		}

		addAll(buildToeMessages(bones))
		addAll(buildTailMessages(bones))
		addAll(buildBustMessages(bones))
		addAll(buildPosteriorMessages(bones))
	}

	return messages.takeIf { it.isNotEmpty() }?.let { OscBundle(1L, it) }
}

internal fun buildYawAlignMessage(headRotation: Quaternion): OscMessage {
	val (_, _, yaw, _) = headRotation.toEulerAngles(EulerOrder.YXZ)
	return OscMessage(
		"/tracking/trackers/head/rotation",
		listOf(
			OscArg.Float(0f),
			OscArg.Float(-yaw * FastMath.RAD_TO_DEG),
			OscArg.Float(0f),
		),
	)
}

private fun positionMessage(address: String, position: Vector3) = OscMessage(
	address,
	listOf(
		OscArg.Float(position.x),
		OscArg.Float(position.y),
		OscArg.Float(-position.z),
	),
)

private fun rotationMessage(address: String, rotation: Quaternion): OscMessage {
	val (_, x, y, z) = Quaternion(rotation.w, -rotation.x, -rotation.y, rotation.z)
		.toEulerAngles(EulerOrder.YXZ)
	return OscMessage(
		address,
		listOf(
			OscArg.Float(x * FastMath.RAD_TO_DEG),
			OscArg.Float(y * FastMath.RAD_TO_DEG),
			OscArg.Float(z * FastMath.RAD_TO_DEG),
		),
	)
}
