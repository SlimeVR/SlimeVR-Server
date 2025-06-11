package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException

class BVHRecorder(server: VRServer) {
	private val poseStreamer: ServerPoseStreamer = ServerPoseStreamer(server)
	private var poseDataStream: PoseDataStream? = null

	fun startRecording() {
		val bvhFile = this.bvhFile
		if (bvhFile != null) {
			try {
				val stream = BVHFileStream(bvhFile)
				poseDataStream = stream
				// 100 FPS
				poseStreamer.setOutput(stream, 1f / 100f)
			} catch (_: IOException) {
				LogManager
					.severe(
						"[BVH] Failed to create the recording file \"" + bvhFile.path + "\".",
					)
			}
		} else {
			LogManager.severe("[BVH] Unable to get file to save to")
		}
	}

	fun endRecording() {
		try {
			val stream = poseDataStream
			if (stream != null) {
				poseStreamer.closeOutput(stream)
			}
		} catch (e1: Exception) {
			LogManager.severe("[BVH] Exception while closing poseDataStream", e1)
		} finally {
			poseDataStream = null
		}
	}

	private val bvhFile: File?
		get() {
			if (bvhSaveDir.isDirectory() || bvhSaveDir.mkdirs()) {
				var saveRecording: File?
				var recordingIndex = 1
				do {
					saveRecording =
						File(bvhSaveDir, "BVH-Recording" + recordingIndex++ + ".bvh")
				} while (saveRecording.exists())

				return saveRecording
			} else {
				LogManager
					.severe(
						"[BVH] Failed to create the recording directory \"${bvhSaveDir.path}\".",
					)
			}

			return null
		}

	val isRecording: Boolean
		get() = poseDataStream != null

	companion object {
		private val bvhSaveDir = File("BVH Recordings")
	}
}
