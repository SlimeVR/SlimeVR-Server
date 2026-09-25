package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart

private const val MAX_VERTICAL_OFFSET = 0.01f

internal fun buildBustMessages(
	bones: Map<BodyPart, BoneState>,
): List<OscContent> {
	val messages = mutableListOf<OscContent>()

	val chest = bones[BodyPart.UPPER_CHEST]
	val leftBust = bones[BodyPart.LEFT_BUST]
	val rightBust = bones[BodyPart.RIGHT_BUST]

	if (chest != null) {
		if (leftBust != null) {
			processBust(messages, chest, leftBust, Side.LEFT)
		}
		if (rightBust != null) {
			processBust(messages, chest, rightBust, Side.RIGHT)
		}
	}

	val verticalOffset = when {
		leftBust != null && rightBust != null ->
			(leftBust.headOffset.y + rightBust.headOffset.y) * 0.5f
		leftBust != null -> leftBust.headOffset.y
		rightBust != null -> rightBust.headOffset.y
		else -> 0f
	}

	addFloat(messages, "BustVertical", (verticalOffset / MAX_VERTICAL_OFFSET).coerceIn(-1f, 1f))

	return messages
}

private fun processBust(
	messages: MutableList<OscContent>,
	chest: BoneState,
	bust: BoneState,
	side: Side,
) {
	val currentRelative = chest.rotation.inv() * bust.rotation
	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)

	val pitch = euler.x * FastMath.RAD_TO_DEG
	val yaw = euler.z * FastMath.RAD_TO_DEG

	addFloat(messages, "${side.oscName}BustPitch", (pitch / 90f).coerceIn(-1f, 1f))
	addFloat(messages, "${side.oscName}BustYaw", (yaw / 90f).coerceIn(-1f, 1f))
}

private fun addFloat(
	messages: MutableList<OscContent>,
	parameterName: String,
	value: Float,
) {
	messages.add(
		OscContent.Message(
			OscMessage(
				"/avatar/parameters/$parameterName",
				listOf(OscArg.Float(value.coerceIn(-1f, 1f))),
			),
		),
	)
}

private val Side.oscName: String
	get() = when (this) {
		Side.LEFT -> "Left"
		Side.RIGHT -> "Right"
	}

