package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException
import java.nio.file.Path

class BVHRecorder(server: VRServer) {
	private val poseStreamer: ServerPoseStreamer = ServerPoseStreamer(server)
	private var poseDataStream: PoseDataStream? = null

	val isRecording: Boolean
		get() = poseDataStream != null

	fun startRecording(path: Path) {
		val filePath = path.toFile()

		val file = if (filePath.isDirectory()) {
			getBvhFile(filePath) ?: return
		} else {
			filePath
		}

		try {
			val stream = BVHFileStream(file)
			poseDataStream = stream
			// 100 FPS
			poseStreamer.setOutput(stream, 1f / 100f)
		} catch (_: IOException) {
			LogManager.severe("[BVH] Failed to create the recording file \"${file.path}\".")
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

	private fun getBvhFile(bvhSaveDir: File): File? {
		if (bvhSaveDir.isDirectory() || bvhSaveDir.mkdirs()) {
			var saveRecording: File?
			var recordingIndex = 1
			do {
				saveRecording =
					File(bvhSaveDir, "BVH-Recording${recordingIndex++}.bvh")
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
}
