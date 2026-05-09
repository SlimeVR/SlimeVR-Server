package dev.slimevr.vrcosc

import dev.slimevr.config.VRCOSCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.PI

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

internal fun buildOutgoingBundle(
	bones: Map<BodyPart, BoneState>,
	config: VRCOSCConfig,
): OscBundle? {
	val messages = buildList {
		for ((bodyPart, trackerId) in trackerIdsByBodyPart) {
			if (!shouldSendTracker(bodyPart, config)) continue

			val bone = bones[bodyPart] ?: continue
			add(
				OscContent.Message(
					positionMessage("/tracking/trackers/$trackerId/position", bone.headPosition),
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
					positionMessage("/tracking/trackers/head/position", head.headPosition),
				),
			)
		}
	}

	return messages.takeIf { it.isNotEmpty() }?.let { OscBundle(1L, it) }
}

internal fun buildYawAlignMessage(headRotation: Quaternion): OscMessage {
	val (_, _, yaw, _) = headRotation.toEulerAngles(EulerOrder.YXZ)
	return OscMessage(
		"/tracking/trackers/head/rotation",
		listOf(
			OscArg.Float(0f),
			OscArg.Float(-yaw * 180f / PI.toFloat()),
			OscArg.Float(0f),
		),
	)
}

private fun shouldSendTracker(bodyPart: BodyPart, config: VRCOSCConfig): Boolean = when (bodyPart) {
	BodyPart.HIP -> config.trackers.waist
	BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> config.trackers.feet
	BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG -> config.trackers.knees
	BodyPart.UPPER_CHEST -> config.trackers.chest
	BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM -> config.trackers.elbows
	else -> false
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
			OscArg.Float(x * 180f / PI.toFloat()),
			OscArg.Float(y * 180f / PI.toFloat()),
			OscArg.Float(z * 180f / PI.toFloat()),
		),
	)
}
