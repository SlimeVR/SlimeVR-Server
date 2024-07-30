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
	private var lastResistance = 0f

	/**
	 * Resets the min resistance from the last resistance value received
	 */
	fun resetMin() {
		minResistance = lastResistance

		setFlexResistance(lastResistance)
		tracker.dataTick()
	}

	/**
	 * Resets the max resistance from the last resistance value received
	 */
	fun resetMax() {
		maxResistance = lastResistance

		setFlexResistance(lastResistance)
		tracker.dataTick()
	}

	/**
	 * Sets the flex resistance which is then calculated into an angle
	 */
	fun setFlexResistance(resistance: Float) {
		// Update min and max if needed
		minResistance = if (minResistance == Float.MIN_VALUE) {
			resistance
		} else if (minResistance > maxResistance) {
			max(minResistance, resistance)
		} else {
			min(minResistance, resistance)
		}
		maxResistance = if (maxResistance == Float.MAX_VALUE) {
			resistance
		} else if (maxResistance < minResistance) {
			min(maxResistance, resistance)
		} else {
			max(maxResistance, resistance)
		}

		// Get max angle
		val maxBend = getMaxAngleForTrackerPosition(tracker.trackerPosition)

		// Get angle and set it
		var angle = maxBend * (resistance - minResistance) / (maxResistance - minResistance)
		if (angle.isNaN()) angle = 0f // div per 0
		setFlexAngle(angle)

		lastResistance = resistance
	}

	/**
	 * Sets an angle (rad) about the X axis
	 */
	fun setFlexAngle(angle: Float) {
		// Create a rotation about the X axis out of the given angle
		tracker.setRotation(EulerAngles(EulerOrder.YZX, angle, 0f, 0f).toQuaternion())
	}

	/**
	 * Gets the max pitch angle for a TrackerPosition
	 */
	// TODO direction changes depending on TrackerPosition. Default should be around X axis.
	// Fingers and shoulders are around Z axis.
	private fun getMaxAngleForTrackerPosition(trackerPosition: TrackerPosition?): Float {
		if (trackerPosition == null) return FastMath.PI // 180 degrees

		return when (trackerPosition) {
			TrackerPosition.LEFT_THUMB_DISTAL, TrackerPosition.LEFT_INDEX_DISTAL,
			TrackerPosition.LEFT_MIDDLE_DISTAL, TrackerPosition.LEFT_RING_DISTAL,
			TrackerPosition.LEFT_LITTLE_DISTAL, TrackerPosition.RIGHT_THUMB_DISTAL,
			TrackerPosition.RIGHT_INDEX_DISTAL, TrackerPosition.RIGHT_MIDDLE_DISTAL,
			TrackerPosition.RIGHT_RING_DISTAL, TrackerPosition.RIGHT_LITTLE_DISTAL,
			-> FastMath.PI + FastMath.HALF_PI

			// 270 degrees

			TrackerPosition.LEFT_THUMB_INTERMEDIATE, TrackerPosition.LEFT_INDEX_INTERMEDIATE,
			TrackerPosition.LEFT_MIDDLE_INTERMEDIATE, TrackerPosition.LEFT_RING_INTERMEDIATE,
			TrackerPosition.LEFT_LITTLE_INTERMEDIATE, TrackerPosition.RIGHT_THUMB_INTERMEDIATE,
			TrackerPosition.RIGHT_INDEX_INTERMEDIATE, TrackerPosition.RIGHT_MIDDLE_INTERMEDIATE,
			TrackerPosition.RIGHT_RING_INTERMEDIATE, TrackerPosition.RIGHT_LITTLE_INTERMEDIATE,
			-> FastMath.PI

			// 180 degrees

			TrackerPosition.LEFT_THUMB_PROXIMAL, TrackerPosition.LEFT_INDEX_PROXIMAL,
			TrackerPosition.LEFT_MIDDLE_PROXIMAL, TrackerPosition.LEFT_RING_PROXIMAL,
			TrackerPosition.LEFT_LITTLE_PROXIMAL, TrackerPosition.RIGHT_THUMB_PROXIMAL,
			TrackerPosition.RIGHT_INDEX_PROXIMAL, TrackerPosition.RIGHT_MIDDLE_PROXIMAL,
			TrackerPosition.RIGHT_RING_PROXIMAL, TrackerPosition.RIGHT_LITTLE_PROXIMAL,
			-> FastMath.HALF_PI

			// 90 degrees

			TrackerPosition.LEFT_SHOULDER, TrackerPosition.RIGHT_SHOULDER -> FastMath.QUARTER_PI

			// 45 degrees

			else -> FastMath.PI // 180 degrees
		}
	}
}
