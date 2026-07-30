package dev.slimevr.vrcosc

import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.*

private enum class FootSide { Left, Right }
internal fun buildToeMessages(bones: Map<BodyPart, BoneState>): List<OscContent> {
	val messages = mutableListOf<OscContent>()

	// LEFT FOOT + TOES
	val leftFoot = bones[BodyPart.LEFT_FOOT]
	if (leftFoot != null) {
		val leftToes = listOf(
			bones[BodyPart.LEFT_BIG_TOE],
			bones[BodyPart.LEFT_INDEX_TOE],
			bones[BodyPart.LEFT_LITTLE_TOE],
		)
		processToesForFoot(leftFoot, leftToes, FootSide.Left, messages)
	}

	// RIGHT FOOT + TOES
	val rightFoot = bones[BodyPart.RIGHT_FOOT]
	if (rightFoot != null) {
		val rightToes = listOf(
			bones[BodyPart.RIGHT_BIG_TOE],
			bones[BodyPart.RIGHT_INDEX_TOE],
			bones[BodyPart.RIGHT_LITTLE_TOE],
		)
		processToesForFoot(rightFoot, rightToes, FootSide.Right, messages)
	}

	return messages
}

private fun processToesForFoot(
	foot: BoneState,
	toeBones: List<BoneState?>,
	side: FootSide,
	messages: MutableList<OscContent>,
) {
	var lastAssigned: BoneState? = null

	for ((segmentIndex, toe) in toeBones.withIndex()) {
		if (toe != null) {
			lastAssigned = toe
		}

		if (lastAssigned == null) continue

		when (segmentIndex) {
			0 -> processToe(foot, lastAssigned, side, 0, oppositeSide(side), messages)

			1 -> {
				processToe(foot, lastAssigned, side, 1, oppositeSide(side), messages)
				processToe(foot, lastAssigned, side, 2, oppositeSide(side), messages)
			}

			2 -> {
				processToe(foot, lastAssigned, side, 3, side, messages)
				processToe(foot, lastAssigned, side, 4, side, messages)
			}
		}
	}
}

private fun oppositeSide(side: FootSide): FootSide = when (side) {
	FootSide.Left -> FootSide.Right
	FootSide.Right -> FootSide.Left
}

private fun processToe(
	foot: BoneState,
	toe: BoneState,
	side: FootSide,
	toeNumber: Int,
	splayDirection: FootSide,
	messages: MutableList<OscContent>,
) {
	val footRot = foot.rotation
	val toeRot = toe.rotation
	val currentRelative = footRot.inv() * toeRot

	val euler = currentRelative.toEulerAngles(EulerOrder.YZX)

	val pitch = euler.z
	val yaw = euler.y
	val tipToe = pitch < -14f
	val bending = pitch > 15f && !tipToe
	val splayed = when (splayDirection) {
		FootSide.Left -> yaw < -7f
		FootSide.Right -> yaw > 7f
	}

	messages.add(OscContent.Message(OscMessage("/avatar/parameters/TipToes${side.name}", listOf(if (tipToe) OscArg.True else OscArg.False))))
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/ToeBent${side.name}${toeNumber + 1}Bool", listOf(if (bending) OscArg.True else OscArg.False))))
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/ToeSplay${side.name}${toeNumber + 1}", listOf(if (splayed) OscArg.True else OscArg.False))))

	val floatValue = (pitch / 90f).coerceIn(-1f, 1f)
	messages.add(OscContent.Message(OscMessage("/avatar/parameters/Toe${side.name}${toeNumber + 1}Float", listOf(OscArg.Float(floatValue)))))
}

private fun quaternionToEulerDegrees(q: Quaternion): Vector3 {
	val sinrCosp = 2f * (q.w * q.x + q.y * q.z)
	val cosrCosp = 1f - 2f * (q.x * q.x + q.y * q.y)
	val roll = atan2(sinrCosp, cosrCosp)

	val sinp = 2f * (q.w * q.y - q.z * q.x)
	val pitch = if (abs(sinp) >= 1f) {
		(PI.toFloat() / 2f) * sign(sinp)
	} else {
		asin(sinp)
	}

	val sinyCosp = 2f * (q.w * q.z + q.x * q.y)
	val cosyCosp = 1f - 2f * (q.y * q.y + q.z * q.z)
	val yaw = atan2(sinyCosp, cosyCosp)

	return Vector3(
		Math.toDegrees(pitch.toDouble()).toFloat(),
		Math.toDegrees(yaw.toDouble()).toFloat(),
		Math.toDegrees(roll.toDouble()).toFloat(),
	)
}
