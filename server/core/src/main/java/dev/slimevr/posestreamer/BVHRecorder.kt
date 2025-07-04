package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import io.eiren.util.logging.LogManager
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.isDirectory

class BVHRecorder(server: VRServer) {
	private val poseStreamer: ServerPoseStreamer = ServerPoseStreamer(server)
	private var poseDataStream: PoseDataStream? = null

	fun startRecording(newPath: Path?) {
		val filePath = newPath?.toFile() ?: return

		val file = if (filePath.isDirectory()) {
			getBvhFile(filePath) ?: return
		} else {
			filePath
		}

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
			LogManager.severe(
				"[BVH] Failed to create the recording directory \"${bvhSaveDir.path}\"",
			)
		}

		return null
	}

	val isRecording: Boolean
		get() = poseDataStream != null
}
