package dev.slimevr.poseframeformat.player

import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.poseframeformat.trackerdata.TrackerFrame
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.trackers.Tracker

class TrackerFramesPlayer(vararg val frameHolders: TrackerFrames) {

	val playerTrackers: Array<PlayerTracker> = frameHolders.map { trackerFrames ->
		PlayerTracker(
			trackerFrames,
			trackerFrames.toTracker(),
		)
	}.toTypedArray()

	val trackers: Array<Tracker> =
		playerTrackers.map { playerTracker -> playerTracker.tracker }.toTypedArray()

	/**
	 * @return The maximum number of [TrackerFrame]s contained within each
	 * [TrackerFrames] in the internal [TrackerFrames] array.
	 * @see [TrackerFrames.frames]
	 * @see [List.size]
	 */
	val maxFrameCount: Int
		get() {
			return frameHolders.maxOfOrNull { tracker -> tracker.frames.size } ?: 0
		}

	constructor(poseFrames: PoseFrames) : this(frameHolders = poseFrames.frameHolders.toTypedArray())

	fun setCursors(index: Int) {
		for (playerTracker in playerTrackers) {
			playerTracker.cursor = index
		}
	}

	fun setScales(scale: Float) {
		for (playerTracker in playerTrackers) {
			playerTracker.scale = scale
		}
	}
}
