package dev.slimevr.tracking.trackers

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import kotlin.math.*

/**
 * Class handling flex sensor data (angle and resistance)
 */
class TrackerFlexHandler(val tracker: Tracker) {
	private var minResistance = 0f
	private var maxResistance = 0f
	private var lastResistance = 0f

	/**
	 * Resets the min resistance from the last resistance value received
	 */
	fun resetMin() {
		minResistance = lastResistance
	}

	/**
	 * Resets the max resistance from the last resistance value received
	 */
	fun maxResistance() {
		maxResistance = lastResistance
	}

	/**
	 * Sets the flex resistance which is then calculated into an angle
	 */
	fun setFlexResistance(resistance: Float) {
		// Update min and max if needed
		minResistance = min(minResistance, resistance)
		maxResistance = max(maxResistance, resistance)

		// Get max angle
		val maxBend = getMaxAngleForTrackerPosition(tracker.trackerPosition)

		// Get angle and set it
		val angle = maxBend * (resistance - minResistance) / (maxResistance - minResistance)
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
