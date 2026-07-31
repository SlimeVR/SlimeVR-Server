package dev.slimevr.vrcosc

import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.*
import dev.slimevr.util.Side
import dev.slimevr.util.opposite

private const val ABSOLUTE_SPLAY_THRESHOLD_ANGLE = 7
private const val MINIMUM_TIP_TOE_PITCH = -14
private const val MINIMUM_BENDING_PITCH = 15
private const val MAXIMUM_ABSOLUTE_TOE_RANGE = 90

internal fun buildToeMessages(bones: Map<BodyPart, BoneState>): List<OscContent> {
	val messages = mutableListOf<OscContent>()

	// LEFT FOOT + TOES
	val leftFoot = bones[BodyPart.LEFT_FOOT]
	if (leftFoot != null) {
		val leftToes = listOf(
			bones[BodyPart.LEFT_BIG_TOE],
			bones[BodyPart.LEFT_INDEX_TOE],
			bones[BodyPart.LEFT_MIDDLE_TOE],
			bones[BodyPart.LEFT_RING_TOE],
			bones[BodyPart.LEFT_LITTLE_TOE],
		)
		processToesForFoot(leftFoot, leftToes, Side.LEFT, messages)
	}

	// RIGHT FOOT + TOES
	val rightFoot = bones[BodyPart.RIGHT_FOOT]
	if (rightFoot != null) {
		val rightToes = listOf(
			bones[BodyPart.RIGHT_BIG_TOE],
			bones[BodyPart.RIGHT_INDEX_TOE],
			bones[BodyPart.RIGHT_MIDDLE_TOE],
			bones[BodyPart.RIGHT_RING_TOE],
			bones[BodyPart.RIGHT_LITTLE_TOE],
		)
		processToesForFoot(rightFoot, rightToes, Side.RIGHT, messages)
	}

	return messages
}

private fun processToesForFoot(
	foot: BoneState,
	toeBones: List<BoneState?>,
	side: Side,
	messages: MutableList<OscContent>,
) {
	var lastAssigned: BoneState? = null

	for ((segmentIndex, toe) in toeBones.withIndex()) {
		if (toe != null) {
			lastAssigned = toe
		}

		if (lastAssigned == null) continue

		val splayDirection = if (segmentIndex <= 2) side.opposite else side

		processToe(foot, lastAssigned, side, segmentIndex, splayDirection, messages)
	}
}

private val Side.oscName: String
	get() = when (this) {
		Side.LEFT -> "Left"
		Side.RIGHT -> "Right"
	}

private fun processToe(
	foot: BoneState,
	toe: BoneState,
	side: Side,
	toeNumber: Int,
	splayDirection: Side,
	messages: MutableList<OscContent>,
) {

	val footRot = foot.rotation
	val toeRot = toe.rotation
	val currentRelative = footRot.inv() * toeRot

	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)

	val pitch = euler.z
	val yaw = euler.y
	val tipToe = pitch < MINIMUM_TIP_TOE_PITCH
	val bending = pitch > MINIMUM_BENDING_PITCH && !tipToe
	val splayed = when (splayDirection) {
		Side.LEFT -> yaw < -ABSOLUTE_SPLAY_THRESHOLD_ANGLE
		Side.RIGHT -> yaw > ABSOLUTE_SPLAY_THRESHOLD_ANGLE
	}
	val floatValue = (pitch / MAXIMUM_ABSOLUTE_TOE_RANGE).coerceIn(-1f, 1f)
	messages.addAll(listOf(OscContent.Message(
		OscMessage("/avatar/parameters/TipToes${side.oscName}", listOf(if (tipToe) OscArg.True else OscArg.False))),
		OscContent.Message(OscMessage("/avatar/parameters/ToeBent${side.oscName}${toeNumber + 1}Bool", listOf(if (bending) OscArg.True else OscArg.False))),
		OscContent.Message(OscMessage("/avatar/parameters/ToeSplay${side.oscName}${toeNumber + 1}", listOf(if (splayed) OscArg.True else OscArg.False))),
		OscContent.Message(OscMessage("/avatar/parameters/Toe${side.oscName}${toeNumber + 1}Float", listOf(OscArg.Float(floatValue))))))
		
}
