package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import dev.slimevr.util.Side
import dev.slimevr.util.opposite
import dev.slimevr.util.side
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart

private const val ABSOLUTE_SPLAY_THRESHOLD_ANGLE = 7
private const val MINIMUM_TIP_TOE_PITCH = -14
private const val MINIMUM_BENDING_PITCH = 15
private const val MAXIMUM_ABSOLUTE_TOE_RANGE = 90

internal fun buildToeMessages(bones: Map<BodyPart, BoneState>): List<OscContent> {
	val messages = mutableListOf<OscContent>()
	processToes(
		bones[BodyPart.LEFT_FOOT],
		arrayOf(
			bones[BodyPart.LEFT_BIG_TOE],
			bones[BodyPart.LEFT_INDEX_TOE],
			bones[BodyPart.LEFT_MIDDLE_TOE],
			bones[BodyPart.LEFT_RING_TOE],
			bones[BodyPart.LEFT_LITTLE_TOE],
		),
		Side.LEFT,
		messages,
	)
	processToes(
		bones[BodyPart.RIGHT_FOOT],
		arrayOf(
			bones[BodyPart.RIGHT_BIG_TOE],
			bones[BodyPart.RIGHT_INDEX_TOE],
			bones[BodyPart.RIGHT_MIDDLE_TOE],
			bones[BodyPart.RIGHT_RING_TOE],
			bones[BodyPart.RIGHT_LITTLE_TOE],
		),
		Side.RIGHT,
		messages,
	)
	return messages
}

private fun processToes(
	foot: BoneState?,
	toeBones: Array<BoneState?>,
	side: Side,
	messages: MutableList<OscContent>,
) {
	if (foot == null) return
	for ((segmentIndex, toe) in toeBones.withIndex()) {
		if (toe == null) continue

		// Big toe goes inwards (the opposite side)
		val splayDirection = if (segmentIndex == 0) side.opposite else side

		processToe(foot, toe, side, segmentIndex, splayDirection, messages)
	}
}

private val Side.oscName
	get() = when (this) {
		Side.LEFT -> "Left"
		Side.RIGHT -> "Right"
	}

private val trueArgs = listOf(OscArg.True)
private val falseArgs = listOf(OscArg.False)

private fun processToe(
	foot: BoneState,
	toe: BoneState,
	side: Side,
	toeNumber: Int,
	splayDirection: Side,
	messages: MutableList<OscContent>,
) {
	val oscToeNumber = toeNumber + 1
	val currentRelative = foot.rotation.inv() * toe.rotation

	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)
	val pitch = euler.x * FastMath.RAD_TO_DEG
	val yaw = euler.y * FastMath.RAD_TO_DEG

	val tipToe = pitch < MINIMUM_TIP_TOE_PITCH
	val bending = pitch > MINIMUM_BENDING_PITCH
	val splayed = when (splayDirection) {
		Side.LEFT -> yaw < -ABSOLUTE_SPLAY_THRESHOLD_ANGLE
		Side.RIGHT -> yaw > ABSOLUTE_SPLAY_THRESHOLD_ANGLE
	}
	val toeCurlValue = (pitch / MAXIMUM_ABSOLUTE_TOE_RANGE).coerceIn(-1f, 1f)
	val toeSplayValue = (yaw / MAXIMUM_ABSOLUTE_TOE_RANGE).coerceIn(-1f, 1f)

	messages.addAll(
		listOf(
			OscContent.Message(OscMessage("/avatar/parameters/TipToes${side.oscName}", if (tipToe) trueArgs else falseArgs)),
			OscContent.Message(OscMessage("/avatar/parameters/ToeBent${side.oscName}${oscToeNumber}Bool", if (bending) trueArgs else falseArgs)),
			OscContent.Message(OscMessage("/avatar/parameters/ToeSplay${side.oscName}$oscToeNumber", if (splayed) trueArgs else falseArgs)),
			OscContent.Message(OscMessage("/avatar/parameters/Toe${side.oscName}${oscToeNumber}Float", listOf(OscArg.Float(toeCurlValue)))),
			OscContent.Message(OscMessage("/avatar/parameters/ToeSplay${side.oscName}${oscToeNumber}Float", listOf(OscArg.Float(toeSplayValue)))),
		),
	)
}
