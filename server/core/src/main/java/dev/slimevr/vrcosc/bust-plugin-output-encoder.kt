package dev.slimevr.vrcosc

import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart

private const val MAXIMUM_ABSOLUTE_BUST_RANGE = 90

internal fun buildBustMessages(bones: Map<BodyPart, BoneState>): List<OscContent> {
	val messages = mutableListOf<OscContent>()
	val chest = bones[BodyPart.CHEST]

	if(chest != null) {
		val leftBust = bones[BodyPart.LEFT_BUST]
		processBust(chest, leftBust, Side.LEFT, messages)

		val rightBust = bones[BodyPart.RIGHT_BUST]
		processBust(chest, rightBust, Side.RIGHT, messages)
	}

	return messages
}

private fun processBust(
	chest: BoneState?,
	bust: BoneState?,
	side: Side,
	messages: MutableList<OscContent>,
) {
	if(bust == null) return

	// Guard against null chest
	if(chest == null) return

	val bustRot = bust.rotation
	val currentRelative = chest.rotation.inv() * bustRot
	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)
	val pitch = Math.toDegrees(euler.x.toDouble()).toFloat()
	val yaw = Math.toDegrees(euler.z.toDouble()).toFloat()
	val bustPitch = (pitch / MAXIMUM_ABSOLUTE_BUST_RANGE).coerceIn(-1f, 1f)
	val bustYaw = (yaw / MAXIMUM_ABSOLUTE_BUST_RANGE).coerceIn(-1f, 1f)
	messages.addAll(
		listOf(
			OscContent.Message(
				OscMessage(
					"/avatar/parameters/${side.oscName}Bust",
					listOf(OscArg.Float(bustPitch))
				)
			),
			OscContent.Message(
				OscMessage(
					"/avatar/parameters/${side.oscName}Bust",
					listOf(OscArg.Float(bustYaw))
				)
			),
		),
	)
}

private val Side.oscName: String
	get() = when (this) {
		Side.LEFT -> "Left"
		Side.RIGHT -> "Right"
	}
