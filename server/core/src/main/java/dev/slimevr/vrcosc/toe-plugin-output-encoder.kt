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

		when (segmentIndex) {
			0 -> {
				processToe(foot, lastAssigned, side, 0, side.opposite, messages)
			}
			1 -> {
				processToe(foot, lastAssigned, side, 1, side.opposite, messages)
			}
			2 -> {
				processToe(foot, lastAssigned, side, 2, side.opposite, messages)
			}
			3 -> {
				processToe(foot, lastAssigned, side, 3, side, messages)
			}
			4 -> {
				processToe(foot, lastAssigned, side, 4, side, messages)
			}
		}
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
	val absoluteSplayThresholdAngle = 7
	val minimumTipToePitch = -14
	val maximumBendingPitch = 15
	val maximumAbsoluteToeRange = 90

	val footRot = foot.rotation
	val toeRot = toe.rotation
	val currentRelative = footRot.inv() * toeRot

	val euler = currentRelative.toEulerAngles(EulerOrder.XYZ)

	val pitch = euler.z
	val yaw = euler.y
	val tipToe = pitch < minimumTipToePitch
	val bending = pitch > maximumBendingPitch && !tipToe
	val splayed = when (splayDirection) {
		Side.LEFT -> yaw < -absoluteSplayThresholdAngle
		Side.RIGHT -> yaw > absoluteSplayThresholdAngle
	}

	messages.add(OscContent.Message(OscMessage("/avatar/parameters/TipToes${side.oscName}", listOf(if (tipToe) OscArg.True else OscArg.False))))
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/ToeBent${side.oscName}${toeNumber + 1}Bool", listOf(if (bending) OscArg.True else OscArg.False))))
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/ToeSplay${side.oscName}${toeNumber + 1}", listOf(if (splayed) OscArg.True else OscArg.False))))

	val floatValue = (pitch / maximumAbsoluteToeRange).coerceIn(-1f, 1f)
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/Toe${side.oscName}${toeNumber + 1}Float", listOf(OscArg.Float(floatValue)))))
}
