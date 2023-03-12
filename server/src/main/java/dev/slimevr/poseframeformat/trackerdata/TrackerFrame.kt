package dev.slimevr.poseframeformat.trackerdata

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

data class TrackerFrame(
	val trackerPosition: TrackerPosition? = null,
	val rotation: Quaternion? = null,
	val position: Vector3? = null,
	val acceleration: Vector3? = null,
	val rawRotation: Quaternion? = null,
) {
	val dataFlags: Int

	val name: String
		get() = "TrackerFrame:/" + (trackerPosition?.designation ?: "null")

	init {
		var initDataFlags = 0

		if (trackerPosition != null) {
			initDataFlags = TrackerFrameData.TRACKER_POSITION_ENUM.add(initDataFlags)
		}
		if (rotation != null) {
			initDataFlags = TrackerFrameData.ROTATION.add(initDataFlags)
		}
		if (position != null) {
			initDataFlags = TrackerFrameData.POSITION.add(initDataFlags)
		}
		if (acceleration != null) {
			initDataFlags = TrackerFrameData.ACCELERATION.add(initDataFlags)
		}
		if (rawRotation != null) {
			initDataFlags = TrackerFrameData.RAW_ROTATION.add(initDataFlags)
		}

		dataFlags = initDataFlags
	}

	fun hasData(flag: TrackerFrameData): Boolean {
		return flag.check(dataFlags)
	}

	// region Tracker Try Getters
	fun tryGetTrackerPosition(): TrackerPosition? {
		return if (hasData(TrackerFrameData.TRACKER_POSITION_ENUM) || hasData(TrackerFrameData.DESIGNATION_STRING)) {
			trackerPosition
		} else {
			null
		}
	}

	fun tryGetRotation(): Quaternion? {
		return if (hasData(TrackerFrameData.ROTATION)) {
			rotation
		} else {
			null
		}
	}

	fun tryGetRawRotation(): Quaternion? {
		return if (hasData(TrackerFrameData.RAW_ROTATION)) {
			rawRotation
		} else {
			null
		}
	}

	fun tryGetPosition(): Vector3? {
		return if (hasData(TrackerFrameData.POSITION)) {
			position
		} else {
			null
		}
	}

	fun tryGetAcceleration(): Vector3? {
		return if (hasData(TrackerFrameData.ACCELERATION)) {
			acceleration
		} else {
			null
		}
	}

	fun hasRotation(): Boolean {
		return hasData(TrackerFrameData.ROTATION)
	}

	fun hasPosition(): Boolean {
		return hasData(TrackerFrameData.POSITION)
	}

	fun hasAcceleration(): Boolean {
		return hasData(TrackerFrameData.ACCELERATION)
	}
	// endregion

	companion object {
		val empty = TrackerFrame()

		fun fromTracker(tracker: Tracker): TrackerFrame? {
			// If the tracker is not ready
			if (tracker.status !== TrackerStatus.OK && tracker.status !== TrackerStatus.BUSY && tracker.status !== TrackerStatus.OCCLUDED) {
				return null
			}
			val designation = tracker.trackerPosition

			// TODO: Discuss with Erimel to fix these
			val rotation: Quaternion? = null
// 			if (tracker.getHasRotation()) {
// 				rotation = new Quaternion();
// 				if (!tracker.getRotation(rotation)) {
// 					// If the get failed, set it back to null
// 					rotation = null;
// 				}
// 			}
			val position: Vector3? = null
// 			if (tracker.getHasPosition()) {
// 				position = new Vector3();
// 				if (!tracker.getPosition(position)) {
// 					// If the get failed, set it back to null
// 					position = null;
// 				}
// 			}
			val acceleration: Vector3? = null
// 			if (tracker.getHasAcceleration()) {
// 				acceleration = new Vector3();
// 				if (!tracker.getAcceleration(acceleration)) {
// 					// If the get failed, set it back to null
// 					acceleration = null;
// 				}
// 			}

			// TODO: Why is there no `hasRawRotation`? Update this to check that
			// first when it exists
			val rawRotation: Quaternion? = null
// 			if (!(tracker.getRawRotation(rawRotation) && !rawRotation.equals(rotation))) {
// 				// If the get failed or the rawRotation is the same as rotation, set
// 				// it back to null
// 				rawRotation = null;
// 			}

			// If tracker has no data at all, there's no point in writing a frame
			return if (designation == null && rotation == null && position == null && acceleration == null && rawRotation == null) {
				null
			} else {
				TrackerFrame(
					designation,
					rotation,
					position,
					acceleration,
					rawRotation
				)
			}
		}
	}
}
