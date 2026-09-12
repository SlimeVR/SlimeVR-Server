package dev.slimevr.vrcosc

import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart

private const val MAXIMUM_ABSOLUTE_TAIL_RANGE = 90

internal fun buildTailMessages(bones: Map<BodyPart, BoneState>): List<OscContent> {
	val messages = mutableListOf<OscContent>()

	val tail = bones[BodyPart.TAIL] ?: return messages
	val hip = bones[BodyPart.HIP] ?: return messages

	processTail(hip, tail, messages)

	return messages
}

private fun processTail(
	hip: BoneState,
	tail: BoneState,
	messages: MutableList<OscContent>,
) {
	val hipRot = hip.rotation
	val tailRot = tail.rotation
	val currentRelative = hipRot.inv() * tailRot

	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)

	val pitch = Math.toDegrees(euler.x.toDouble()).toFloat()
	val yaw = Math.toDegrees(euler.y.toDouble()).toFloat()

	val tailPitchValue = (pitch / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)
	val tailYawValue = (yaw / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)

	messages.addAll(
		listOf(
			OscContent.Message(OscMessage("/avatar/parameters/TailPitchFloat", listOf(OscArg.Float(tailPitchValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailYawFloat", listOf(OscArg.Float(tailYawValue)))),
		),
	)
}

