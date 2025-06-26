package dev.slimevr.posestreamer

import dev.slimevr.poseframeformat.PfrIO.readFromFile
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.poseframeformat.player.TrackerFramesPlayer
import dev.slimevr.tracking.processor.HumanPoseManager
import java.io.File

class PoseFrameStreamer : PoseStreamer {

	val player: TrackerFramesPlayer
	val hpm: HumanPoseManager

	private constructor(
		player: TrackerFramesPlayer,
		hpm: HumanPoseManager,
	) : super(hpm.skeleton) {
		this.player = player
		this.hpm = hpm
	}

	constructor(player: TrackerFramesPlayer) : this(player, HumanPoseManager(player.trackers.toList()))
	constructor(poseFrames: PoseFrames) : this(TrackerFramesPlayer(poseFrames))
	constructor(file: File) : this(readFromFile(file))
	constructor(path: String) : this(File(path))

	@Synchronized
	fun streamAllFrames() {
		for (i in 0 until player.maxFrameCount) {
			player.setCursors(i)
			hpm.update()
			captureFrame()
		}
	}
}
