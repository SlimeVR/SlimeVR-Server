package dev.slimevr.autobone

import dev.slimevr.config.AutoBoneConfig
import dev.slimevr.config.ConfigManager
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.poseframeformat.player.TrackerFramesPlayer
import dev.slimevr.tracking.processor.HumanPoseManager
import java.util.function.Consumer
import kotlin.random.Random

class PoseFrameStep<T>(
	val config: AutoBoneConfig,
	serverConfig: ConfigManager? = null,
	val frames: PoseFrames,
	val preEpoch: Consumer<PoseFrameStep<T>>? = null,
	val onStep: Consumer<PoseFrameStep<T>>,
	val postEpoch: Consumer<PoseFrameStep<T>>? = null,
	var epoch: Int = 0,
	var cursor1: Int = 0,
	var cursor2: Int = 0,
	randomSeed: Long = 0,
	val data: T,
) {
	var maxFrameCount = frames.maxFrameCount

	val framePlayer1 = TrackerFramesPlayer(frames)
	val framePlayer2 = TrackerFramesPlayer(frames)

	val trackers1 = framePlayer1.trackers.toList()
	val trackers2 = framePlayer2.trackers.toList()

	val skeleton1 = HumanPoseManager(trackers1)
	val skeleton2 = HumanPoseManager(trackers2)

	val random = Random(randomSeed)

	init {
		// Load server configs into the skeleton
		if (serverConfig != null) {
			skeleton1.loadFromConfig(serverConfig)
			skeleton2.loadFromConfig(serverConfig)
		}
		// Disable leg tweaks and IK solver, these will mess with the resulting positions
		skeleton1.setLegTweaksEnabled(false)
		skeleton2.setLegTweaksEnabled(false)
	}

	fun setCursors(cursor1: Int, cursor2: Int, updatePlayerCursors: Boolean) {
		this.cursor1 = cursor1
		this.cursor2 = cursor2

		if (updatePlayerCursors) {
			updatePlayerCursors()
		}
	}

	fun updatePlayerCursors() {
		framePlayer1.setCursors(cursor1)
		framePlayer2.setCursors(cursor2)
		skeleton1.update()
		skeleton2.update()
	}
}
