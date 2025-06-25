package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import dev.slimevr.config.ConfigManager
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createTempFile

class BVHRecorder(server: VRServer) {
	private val poseStreamer: ServerPoseStreamer = ServerPoseStreamer(server)
	private var poseDataStream: PoseDataStream? = null
	private var tempFile: File? = null

	fun startRecording() {
		tempFile = createTempFile("slimevr-bvh").toFile()
		if (tempFile != null) {
			try {
				poseDataStream = BVHFileStream(tempFile!!)
				poseStreamer.setOutput(poseDataStream, 1000L / 100L)
			} catch (_: IOException) {
				LogManager
					.severe(
						"[BVH] Failed to create the recording file \"${tempFile!!.path}\".",
					)
			}
		} else {
			LogManager.severe("[BVH] Unable to get file to save to")
		}
	}

	fun endRecording(newPath: Path?) {
		try {
			poseStreamer.closeOutput(poseDataStream)
			if (newPath != null && tempFile != null) {
				ConfigManager.atomicMove(tempFile!!.toPath(), newPath)
			}
		} catch (e: Exception) {
			LogManager.severe("[BVH] Exception while closing poseDataStream", e)
		} finally {
			poseDataStream = null
			tempFile = null
		}
	}

	val isRecording: Boolean
		get() = poseDataStream != null
}
