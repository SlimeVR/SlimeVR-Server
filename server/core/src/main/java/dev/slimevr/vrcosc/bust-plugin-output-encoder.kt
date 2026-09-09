package dev.slimevr.vrcosc

import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.EulerOrder
import solarxr_protocol.datatypes.BodyPart
import dev.slimevr.util.Side
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import kotlin.math.abs
import kotlin.math.exp
private val bustOutputState = BustOutputState()

internal fun buildBustMessages(
	bones: Map<BodyPart, BoneState>,
): List<OscContent> =
	bustOutputState.buildMessages(bones)

private class BustOutputState {
	private var verticalBaselineY = 0f
	private var verticalPosition = 0f
	private var verticalVelocity = 0f
	private var lastUpdateNanos = 0L
	private var baselineInitialized = false

	fun buildMessages(
		bones: Map<BodyPart, BoneState>,
	): List<OscContent> {
		val messages = mutableListOf<OscContent>()

		val chest = bones[BodyPart.CHEST]
		val leftBust = bones[BodyPart.LEFT_BUST]
		val rightBust = bones[BodyPart.RIGHT_BUST]

		if (chest != null) {
			if (leftBust != null) {
				processBust(
					messages,
					chest,
					leftBust,
					Side.LEFT,
				)
			}

			if (rightBust != null) {
				processBust(
					messages,
					chest,
					rightBust,
					Side.RIGHT,
				)
			}
		}

		val verticalAccelerationY =
			getSharedVerticalAccelerationY(
				leftBust,
				rightBust,
			)

		val vertical =
			updateVerticalResponse(
				verticalAccelerationY,
				getDeltaTimeSeconds(),
			)

		addFloat(
			messages,
			"BustVertical",
			vertical,
		)

		return messages
	}

	private fun processBust(
		messages: MutableList<OscContent>,
		chest: BoneState,
		bust: BoneState,
		side: Side,
	) {
		val currentRelative =
			chest.rotation.inv() *
				bust.rotation

		val euler =
			currentRelative.toEulerAngles(
				EulerOrder.XYZ,
			)

		val pitch =
			Math.toDegrees(
				euler.x.toDouble(),
			).toFloat()

		val yaw =
			Math.toDegrees(
				euler.z.toDouble(),
			).toFloat()

		addFloat(
			messages,
			"${side.oscName}BustPitch",
			(pitch / 90f)
				.coerceIn(-1f, 1f),
		)

		addFloat(
			messages,
			"${side.oscName}BustYaw",
			(yaw / 90f)
				.coerceIn(-1f, 1f),
		)
	}

	private fun getSharedVerticalAccelerationY(
		leftBust: BoneState?,
		rightBust: BoneState?,
	): Float? =
		when {
			leftBust != null &&
				rightBust != null ->
				(
					leftBust.acceleration.y +
						rightBust.acceleration.y
					) * 0.5f

			leftBust != null ->
				leftBust.acceleration.y

			rightBust != null ->
				rightBust.acceleration.y

			else ->
				null
		}

	private fun updateVerticalResponse(
		accelerationY: Float?,
		dt: Float,
	): Float {
		if (accelerationY != null) {
			if (!baselineInitialized) {
				verticalBaselineY =
					accelerationY

				baselineInitialized =
					true
			}

			val baselineAlpha =
				1f -
					exp(
						-dt /
							VERTICAL_BASELINE_TIME_CONSTANT,
					)

			verticalBaselineY +=
				(accelerationY -
					verticalBaselineY) *
					baselineAlpha

			var dynamicAcceleration =
				accelerationY -
					verticalBaselineY

			if (
				abs(dynamicAcceleration) <
				VERTICAL_ACCEL_DEADZONE
			) {
				dynamicAcceleration = 0f
			}

			val inertialInput =
				-dynamicAcceleration *
					VERTICAL_ACCEL_GAIN

			stepSpring(
				inertialInput,
				dt,
			)
		} else {
			stepSpring(
				0f,
				dt,
			)
		}

		if (
			abs(verticalPosition) <
			SNAP_POSITION_EPSILON &&
			abs(verticalVelocity) <
			SNAP_VELOCITY_EPSILON
		) {
			verticalPosition = 0f
			verticalVelocity = 0f
		}

		return verticalPosition
			.coerceIn(-1f, 1f)
	}

	private fun stepSpring(
		input: Float,
		dt: Float,
	) {
		val restoring =
			-VERTICAL_SPRING *
				verticalPosition

		val damping =
			-VERTICAL_DAMPING *
				verticalVelocity

		verticalVelocity +=
			(
				input +
					restoring +
					damping
				) * dt

		verticalPosition +=
			verticalVelocity * dt

		if (verticalPosition > 1f) {
			verticalPosition = 1f

			if (verticalVelocity > 0f) {
				verticalVelocity = 0f
			}
		} else if (verticalPosition < -1f) {
			verticalPosition = -1f

			if (verticalVelocity < 0f) {
				verticalVelocity = 0f
			}
		}
	}

	private fun getDeltaTimeSeconds(): Float {
		val now =
			System.nanoTime()

		if (lastUpdateNanos == 0L) {
			lastUpdateNanos = now
			return DEFAULT_FRAME_DT
		}

		val dt =
			(
				(now - lastUpdateNanos)
					.toDouble() /
					1_000_000_000.0
				)
				.toFloat()
				.coerceIn(
					MIN_FRAME_DT,
					MAX_FRAME_DT,
				)

		lastUpdateNanos = now

		return dt
	}

	companion object {
		private const val VERTICAL_ACCEL_DEADZONE =
			0.025f

		private const val VERTICAL_ACCEL_GAIN =
			18f

		private const val VERTICAL_SPRING =
			34f

		private const val VERTICAL_DAMPING =
			9f

		private const val VERTICAL_BASELINE_TIME_CONSTANT =
			2.5f

		private const val DEFAULT_FRAME_DT =
			1f / 60f

		private const val MIN_FRAME_DT =
			1f / 240f

		private const val MAX_FRAME_DT =
			0.05f

		private const val SNAP_POSITION_EPSILON =
			0.0025f

		private const val SNAP_VELOCITY_EPSILON =
			0.01f
	}
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
				listOf(
					OscArg.Float(
						value.coerceIn(
							-1f,
							1f,
						),
					),
				),
			),
		),
	)
}

private val Side.oscName: String
	get() =
		when (this) {
			Side.LEFT -> "Left"
			Side.RIGHT -> "Right"
		}
