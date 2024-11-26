package dev.slimevr.tracking.trackers

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import kotlin.math.*

/**
 * Class handling flex sensor data (angle and resistance)
 * Resistance is expected to go up with bend by default, but a mounting reset allows the contrary
 */
class TrackerFlexHandler(val tracker: Tracker) {
	private var minResistance = Float.MIN_VALUE
	private var maxResistance = Float.MAX_VALUE

	// Used to support resistance going both ways.
	// Default is higher = more bend, but can change after a full and mounting reset.
	private var lastMinResetResistance = Float.MIN_VALUE
	private var resistanceReversed = false
	private var lastResistance = 0f
	private val thumbInitialOffset = FastMath.PI / 8 // 22.5 deg

	/**
	 * Resets the min resistance from the last resistance value received.
	 * Triggered from full reset
	 */
	fun resetMin() {
		minResistance = lastResistance
		lastMinResetResistance = lastResistance

		setFlexResistance(lastResistance)
		tracker.dataTick()
	}

	/**
	 * Resets the max resistance from the last resistance value received.
	 * Triggered from mounting reset
	 */
	fun resetMax() {
		// Account for the resistance being able to be reversed
		if (resistanceReversed != lastResistance < lastMinResetResistance) {
			// Switching
			resistanceReversed = lastResistance < lastMinResetResistance
			minResistance = maxResistance
			maxResistance = lastMinResetResistance
		} else {
			// Not switching
			maxResistance = lastResistance
		}

		setFlexResistance(lastResistance)
		tracker.dataTick()
	}

	/**
	 * Sets the flex resistance which is then calculated into an angle
	 */
	fun setFlexResistance(resistance: Float) {
		// Dynamically calibrate the minimum resistance
		minResistance = if (minResistance == Float.MIN_VALUE) {
			resistance
		} else if (!resistanceReversed) {
			min(minResistance, resistance)
		} else {
			max(minResistance, resistance)
		}

		// Dynamically calibrate the maximum resistance
		maxResistance = if (maxResistance == Float.MAX_VALUE) {
			resistance
		} else if (!resistanceReversed) {
			max(maxResistance, resistance)
		} else {
			min(maxResistance, resistance)
		}

		// Get max angle
		val maxBend = getMaxAngleForTrackerPosition(tracker.trackerPosition)

		// Get angle and set it
		val angle = if (minResistance == maxResistance) {
			// Avoid division by 0
			0f
		} else {
			maxBend * (resistance - minResistance) / (maxResistance - minResistance)
		}
		setFlexAngle(angle)

		lastResistance = resistance
	}

	/**
	 * Sets an angle (rad) about the X axis
	 */
	fun setFlexAngle(angle: Float) {
		// Sets the rotation of the tracker by the angle about a given axis depending on
		// the tracker's TrackerPosition
		when (tracker.trackerPosition) {
			TrackerPosition.LEFT_INDEX_PROXIMAL, TrackerPosition.LEFT_INDEX_INTERMEDIATE,
			TrackerPosition.LEFT_INDEX_DISTAL, TrackerPosition.LEFT_MIDDLE_PROXIMAL,
			TrackerPosition.LEFT_MIDDLE_INTERMEDIATE, TrackerPosition.LEFT_MIDDLE_DISTAL,
			TrackerPosition.LEFT_RING_PROXIMAL, TrackerPosition.LEFT_RING_INTERMEDIATE,
			TrackerPosition.LEFT_RING_DISTAL, TrackerPosition.LEFT_LITTLE_PROXIMAL,
			TrackerPosition.LEFT_LITTLE_INTERMEDIATE, TrackerPosition.LEFT_LITTLE_DISTAL,
			TrackerPosition.RIGHT_SHOULDER,
			-> tracker.setRotation(EulerAngles(EulerOrder.YZX, 0f, 0f, angle).toQuaternion())

			TrackerPosition.RIGHT_INDEX_PROXIMAL, TrackerPosition.RIGHT_INDEX_INTERMEDIATE,
			TrackerPosition.RIGHT_INDEX_DISTAL, TrackerPosition.RIGHT_MIDDLE_PROXIMAL,
			TrackerPosition.RIGHT_MIDDLE_INTERMEDIATE, TrackerPosition.RIGHT_MIDDLE_DISTAL,
			TrackerPosition.RIGHT_RING_PROXIMAL, TrackerPosition.RIGHT_RING_INTERMEDIATE,
			TrackerPosition.RIGHT_RING_DISTAL, TrackerPosition.RIGHT_LITTLE_PROXIMAL,
			TrackerPosition.RIGHT_LITTLE_INTERMEDIATE, TrackerPosition.RIGHT_LITTLE_DISTAL,
			TrackerPosition.LEFT_SHOULDER,
			-> tracker.setRotation(EulerAngles(EulerOrder.YZX, 0f, 0f, -angle).toQuaternion())

			TrackerPosition.LEFT_THUMB_METACARPAL, TrackerPosition.LEFT_THUMB_PROXIMAL, TrackerPosition.LEFT_THUMB_DISTAL,
			-> tracker.setRotation(EulerAngles(EulerOrder.YZX, thumbInitialOffset - angle, -angle * 0.05f, angle * 0.1f).toQuaternion())

			TrackerPosition.RIGHT_THUMB_METACARPAL, TrackerPosition.RIGHT_THUMB_PROXIMAL, TrackerPosition.RIGHT_THUMB_DISTAL,
			-> tracker.setRotation(EulerAngles(EulerOrder.YZX, thumbInitialOffset - angle, angle * 0.05f, -angle * 0.1f).toQuaternion())

			// Default to X axis (pitch)
			else -> tracker.setRotation(EulerAngles(EulerOrder.YZX, angle, 0f, 0f).toQuaternion())
		}
	}

	/**
	 * Gets the max angle for a TrackerPosition
	 */
	private fun getMaxAngleForTrackerPosition(trackerPosition: TrackerPosition?): Float {
		if (trackerPosition == null) return FastMath.PI // 180 degrees

		return when (trackerPosition) {
			// 270 degrees
			TrackerPosition.LEFT_INDEX_DISTAL, TrackerPosition.LEFT_MIDDLE_DISTAL,
			TrackerPosition.LEFT_RING_DISTAL, TrackerPosition.LEFT_LITTLE_DISTAL,
			TrackerPosition.RIGHT_INDEX_DISTAL, TrackerPosition.RIGHT_MIDDLE_DISTAL,
			TrackerPosition.RIGHT_RING_DISTAL, TrackerPosition.RIGHT_LITTLE_DISTAL,
			-> FastMath.PI + FastMath.HALF_PI

			// 202.5 degrees
			TrackerPosition.LEFT_THUMB_DISTAL, TrackerPosition.RIGHT_THUMB_DISTAL,
			-> FastMath.PI + thumbInitialOffset

			// 180 degrees
			TrackerPosition.LEFT_INDEX_INTERMEDIATE, TrackerPosition.LEFT_MIDDLE_INTERMEDIATE,
			TrackerPosition.LEFT_RING_INTERMEDIATE, TrackerPosition.LEFT_LITTLE_INTERMEDIATE,
			TrackerPosition.RIGHT_INDEX_INTERMEDIATE, TrackerPosition.RIGHT_MIDDLE_INTERMEDIATE,
			TrackerPosition.RIGHT_RING_INTERMEDIATE, TrackerPosition.RIGHT_LITTLE_INTERMEDIATE,
			-> FastMath.PI

			// 112.5 degrees
			TrackerPosition.LEFT_THUMB_PROXIMAL, TrackerPosition.RIGHT_THUMB_PROXIMAL,
			-> FastMath.HALF_PI + thumbInitialOffset

			// 90 degrees
			TrackerPosition.LEFT_INDEX_PROXIMAL, TrackerPosition.LEFT_MIDDLE_PROXIMAL,
			TrackerPosition.LEFT_RING_PROXIMAL, TrackerPosition.LEFT_LITTLE_PROXIMAL,
			TrackerPosition.RIGHT_INDEX_PROXIMAL, TrackerPosition.RIGHT_MIDDLE_PROXIMAL,
			TrackerPosition.RIGHT_RING_PROXIMAL, TrackerPosition.RIGHT_LITTLE_PROXIMAL,
			-> FastMath.HALF_PI

			// 67.5 degrees
			TrackerPosition.LEFT_THUMB_METACARPAL, TrackerPosition.RIGHT_THUMB_METACARPAL,
			-> FastMath.QUARTER_PI + thumbInitialOffset

			// 45 degrees
			TrackerPosition.LEFT_SHOULDER, TrackerPosition.RIGHT_SHOULDER,
			-> FastMath.QUARTER_PI

			// 135 degrees
			else -> FastMath.HALF_PI + FastMath.QUARTER_PI
		}
	}
}
