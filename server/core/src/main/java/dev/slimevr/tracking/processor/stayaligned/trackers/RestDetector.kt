package dev.slimevr.tracking.processor.stayaligned.trackers

import dev.slimevr.math.Angle
import io.github.axisangles.ktmath.Quaternion
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Detects whether a tracker is at rest.
 *
 * A tracker is at rest when it stays within a certain rotational range for a given
 * amount of time. If it rotates past that range, it is no longer at rest.
 *
 * TODO: In practice this is good enough for Stay Aligned, but we could also consider
 * 		acceleration if we want to make this a general purpose rest detector.
 */
class RestDetector(
	private val maxRotation: Angle,
	private val enterRestTime: Duration,
	private val enterMovingTime: Duration,
) {
	enum class State {
		MOVING,
		AT_REST,
		RECENTLY_AT_REST,
	}

	var state = State.MOVING
		private set

	// Instant that we entered the current state
	private var startTime = TimeSource.Monotonic.markNow()

	// Rotation which could
	private var lastRotation = Quaternion.IDENTITY
	private var lastRotationTime = TimeSource.Monotonic.markNow()

	/**
	 * Resets the detector
	 */
	fun reset() {
		val now = TimeSource.Monotonic.markNow()

		state = State.MOVING
		startTime = now
		lastRotation = Quaternion.IDENTITY
		lastRotationTime = now
	}

	/**
	 * Provides a new rotation sample to the detector.
	 *
	 * @return whether the tracker is at rest
	 */
	fun update(rotation: Quaternion) {
		val now = TimeSource.Monotonic.markNow()

		if (
			state == State.RECENTLY_AT_REST &&
			now > startTime.plus(enterMovingTime)
		) {
			state = State.MOVING
			startTime = now
			lastRotation = rotation
			lastRotationTime = now
		}

		when (state) {
			State.MOVING,
			State.RECENTLY_AT_REST,
			->
				if (Angle.absBetween(lastRotation, rotation) > maxRotation) {
					lastRotation = rotation
					lastRotationTime = now
				} else {
					// When we detect the tracker is at rest, use the current rotation as the
					// new start rotation for continuing to detect the tracker is at rest
					if (now > lastRotationTime.plus(enterRestTime)) {
						state = State.AT_REST
						startTime = now
						lastRotation = rotation
						lastRotationTime = now
					}
				}

			State.AT_REST ->
				if (Angle.absBetween(lastRotation, rotation) > maxRotation) {
					state = State.RECENTLY_AT_REST
					startTime = now
					lastRotation = rotation
					lastRotationTime = now
				}
		}
	}
}
