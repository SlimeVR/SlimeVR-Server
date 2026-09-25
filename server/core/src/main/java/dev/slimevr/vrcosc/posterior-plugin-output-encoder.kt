package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BodyPart
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder

private const val MAX_VERTICAL_OFFSET = 0.01f

internal fun buildPosteriorMessages(
	bones: Map<BodyPart, BoneState>,
): List<OscContent> {
	val messages = mutableListOf<OscContent>()

	val hip = bones[BodyPart.HIP]
	val leftPosterior = bones[BodyPart.LEFT_POSTERIOR]
	val rightPosterior = bones[BodyPart.RIGHT_POSTERIOR]

	if (hip != null) {
		if (leftPosterior != null) {
			processPosterior(messages, hip, leftPosterior, Side.LEFT)
		}
		if (rightPosterior != null) {
			processPosterior(messages, hip, rightPosterior, Side.RIGHT)
		}
	}

	val verticalOffset = when {
		leftPosterior != null && rightPosterior != null ->
			(leftPosterior.headOffset.y + rightPosterior.headOffset.y) * 0.5f
		leftPosterior != null -> leftPosterior.headOffset.y
		rightPosterior != null -> rightPosterior.headOffset.y
		else -> 0f
	}

	addFloat(messages, "PosteriorVertical", (verticalOffset / MAX_VERTICAL_OFFSET).coerceIn(-1f, 1f))

	return messages
}

private fun processPosterior(
	messages: MutableList<OscContent>,
	hip: BoneState,
	posterior: BoneState,
	side: Side,
) {
	val currentRelative = hip.rotation.inv() * posterior.rotation
	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)

	val pitch = euler.x * FastMath.RAD_TO_DEG
	val yaw = euler.z * FastMath.RAD_TO_DEG

	addFloat(messages, "${side.oscName}PosteriorPitch", (pitch / 90f).coerceIn(-1f, 1f))
	addFloat(messages, "${side.oscName}PosteriorYaw", (yaw / 90f).coerceIn(-1f, 1f))
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
