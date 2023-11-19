package dev.slimevr.poseframeformat.player

import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.trackers.Tracker

class PlayerTracker(val trackerFrames: TrackerFrames, val tracker: Tracker, private var internalCursor: Int = 0, private var internalScale: Float = 1f) {

	var cursor: Int
		get() = internalCursor
		set(value) {
			val limitedCursor = limitCursor(value)
			internalCursor = limitedCursor
			setTrackerStateFromIndex(limitedCursor)
		}

	var scale: Float
		get() = internalScale
		set(value) {
			internalScale = value
			setTrackerStateFromIndex()
		}

	init {
		setTrackerStateFromIndex(limitCursor())
	}

	fun limitCursor(cursor: Int): Int {
		return if (cursor < 0 || trackerFrames.frames.isEmpty()) {
			return 0
		} else if (cursor >= trackerFrames.frames.size) {
			return trackerFrames.frames.size - 1
		} else {
			cursor
		}
	}

	fun limitCursor(): Int {
		val limitedCursor = limitCursor(internalCursor)
		internalCursor = limitedCursor
		return limitedCursor
	}

	private fun setTrackerStateFromIndex(index: Int = internalCursor) {
		val frame = trackerFrames.tryGetFrame(index) ?: return

		/*
		 * TODO: No way to set adjusted rotation manually? That might be nice to have...
		 * for now we'll stick with just setting the final rotation as raw and not
		 * enabling any adjustments
		 */

		val trackerPosition = frame.tryGetTrackerPosition()
		if (trackerPosition != null) {
			tracker.trackerPosition = trackerPosition
		}

		val rotation = frame.tryGetRotation()
		if (rotation != null) {
			tracker.setRotation(rotation)
		}

		val position = frame.tryGetPosition()
		if (position != null) {
			tracker.position = position * internalScale
		}

		val acceleration = frame.tryGetAcceleration()
		if (acceleration != null) {
			tracker.setAcceleration(acceleration * internalScale)
		}
	}
}
