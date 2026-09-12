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
	val tailTip = bones[BodyPart.TAIL_6] ?: tail

	processTail(hip, tail, tailTip, messages)

	return messages
}

private fun processTail(
	hip: BoneState,
	tail: BoneState,
	tailTip: BoneState,
	messages: MutableList<OscContent>,
) {
	val hipRot = hip.rotation
	val tailRot = tail.rotation
	val baseRelative = hipRot.inv() * tailRot

	val baseEuler = baseRelative.toEulerAngles(EulerOrder.XYZ)
	val basePitch = Math.toDegrees(baseEuler.x.toDouble()).toFloat()
	val baseYaw = Math.toDegrees(baseEuler.y.toDouble()).toFloat()

	val tailPitchValue = (basePitch / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)
	val tailYawValue = (baseYaw / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)

	// Tip rotation relative to hip
	val tipRelative = hipRot.inv() * tailTip.rotation
	val tipEuler = tipRelative.toEulerAngles(EulerOrder.XYZ)
	val tipPitch = Math.toDegrees(tipEuler.x.toDouble()).toFloat()
	val tipYaw = Math.toDegrees(tipEuler.y.toDouble()).toFloat()

	val tailTipPitchValue = (tipPitch / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)
	val tailTipYawValue = (tipYaw / MAXIMUM_ABSOLUTE_TAIL_RANGE).coerceIn(-1f, 1f)

	// Tip 3D position relative to hip
	val tipPositionRel = hipRot.inv().sandwich(tailTip.tailPosition - hip.tailPosition)

	messages.addAll(
		listOf(
			OscContent.Message(OscMessage("/avatar/parameters/TailPitchFloat", listOf(OscArg.Float(tailPitchValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailYawFloat", listOf(OscArg.Float(tailYawValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailTipPitchFloat", listOf(OscArg.Float(tailTipPitchValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailTipYawFloat", listOf(OscArg.Float(tailTipYawValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailTipPositionX", listOf(OscArg.Float(tipPositionRel.x)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailTipPositionY", listOf(OscArg.Float(tipPositionRel.y)))),
			OscContent.Message(OscMessage("/avatar/parameters/TailTipPositionZ", listOf(OscArg.Float(tipPositionRel.z)))),
		),
	)
}



