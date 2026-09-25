package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs
import kotlin.math.exp

private const val BUST_NEUTRAL_TIME_CONSTANT = 5f
private const val BUST_MIN_DISPLACEMENT = 0.001f

private class BustVerticalOutputState {
	private var neutralY = Float.NaN
	private var maxDisplacement = BUST_MIN_DISPLACEMENT
	private var lastNanos = 0L

	fun update(currentY: Float): Float {
		val now = System.nanoTime()
		val dt = if (lastNanos == 0L) (1f / 60f)
		          else ((now - lastNanos).toDouble() / 1_000_000_000.0).toFloat().coerceIn(0f, 0.1f)
		lastNanos = now

		if (neutralY.isNaN()) {
			neutralY = currentY
			return 0f
		}

		neutralY += (currentY - neutralY) * (1f - exp(-dt / BUST_NEUTRAL_TIME_CONSTANT))

		val displacement = currentY - neutralY

		val absDis = abs(displacement)
		if (absDis > maxDisplacement) maxDisplacement = absDis

		val normalized = (displacement / maxDisplacement).coerceIn(-1f, 1f)
		return if (kotlin.math.abs(normalized) < 0.01f) 0f else normalized
	}
}

private val bustVerticalState = BustVerticalOutputState()

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

	val currentY = when {
		leftBust != null && rightBust != null ->
			(leftBust.headOffset.y + rightBust.headOffset.y) * 0.5f
		leftBust != null -> leftBust.headOffset.y
		rightBust != null -> rightBust.headOffset.y
		else -> Float.NaN
	}

	val bustVertical = if (currentY.isNaN()) 0f else bustVerticalState.update(currentY)
	addFloat(messages, "BustVertical", bustVertical)

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
