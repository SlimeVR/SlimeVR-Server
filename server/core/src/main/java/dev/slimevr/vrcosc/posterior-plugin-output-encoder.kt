package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BodyPart
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder
import kotlin.math.abs
import kotlin.math.exp

private const val POSTERIOR_NEUTRAL_TIME_CONSTANT = 5f
private const val POSTERIOR_MIN_DISPLACEMENT = 0.001f

private class PosteriorVerticalOutputState {
	private var neutralY = Float.NaN
	private var maxDisplacement = POSTERIOR_MIN_DISPLACEMENT
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

		neutralY += (currentY - neutralY) * (1f - exp(-dt / POSTERIOR_NEUTRAL_TIME_CONSTANT))

		val displacement = currentY - neutralY

		val absDis = abs(displacement)
		if (absDis > maxDisplacement) maxDisplacement = absDis

		val normalized = (displacement / maxDisplacement).coerceIn(-1f, 1f)
		return if (kotlin.math.abs(normalized) < 0.01f) 0f else normalized
	}
}

private val posteriorVerticalState = PosteriorVerticalOutputState()

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

	val currentY = when {
		leftPosterior != null && rightPosterior != null ->
			(leftPosterior.headOffset.y + rightPosterior.headOffset.y) * 0.5f
		leftPosterior != null -> leftPosterior.headOffset.y
		rightPosterior != null -> rightPosterior.headOffset.y
		else -> Float.NaN
	}

	val posteriorVertical = if (currentY.isNaN()) 0f else posteriorVerticalState.update(currentY)
	addFloat(messages, "PosteriorVertical", posteriorVertical)

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
