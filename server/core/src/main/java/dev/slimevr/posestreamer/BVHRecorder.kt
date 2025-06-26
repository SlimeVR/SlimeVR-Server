package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.nio.file.Path

class BVHRecorder(server: VRServer) {
	private val poseStreamer: ServerPoseStreamer = ServerPoseStreamer(server)
	private var poseDataStream: PoseDataStream? = null

	fun startRecording(newPath: Path?) {
		val file = newPath?.toFile() ?: return
		try {
			poseDataStream = BVHFileStream(file)
			poseStreamer.setOutput(poseDataStream, 1000L / 100L)
		} catch (_: IOException) {
			LogManager
				.severe(
					"[BVH] Failed to create the recording file \"${file.path}\".",
				)
		}
	}

	fun endRecording() {
		try {
			poseStreamer.closeOutput(poseDataStream)
		} catch (e: Exception) {
			LogManager.severe("[BVH] Exception while closing poseDataStream", e)
		} finally {
			poseDataStream = null
		}
	}

	val isRecording: Boolean
		get() = poseDataStream != null
}
