package dev.slimevr.bvh

import dev.slimevr.AppLogger
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Path

private suspend fun resolveBvhFile(path: Path): File? = withContext(Dispatchers.IO) {
	val file = path.toFile()
	if (file.isDirectory) {
		getBvhFileInDir(file)
	} else {
		if (file.name.lowercase().endsWith(".bvh")) file else null
	}
}

private suspend fun getBvhFileInDir(dir: File): File? = withContext(Dispatchers.IO) {
	if (dir.isDirectory || dir.mkdirs()) {
		var file: File
		var index = 1
		do {
			file = File(dir, "BVH-Recording${index++}.bvh")
		} while (file.exists())
		file
	} else {
		AppLogger.bvh.error("Failed to create recording directory \"${dir.path}\"")
		null
	}
}

class BVHRecordingBehaviour(private val skeleton: Skeleton) : BVHBehaviourType {
	override fun reduce(state: BVHState, action: BVHActions) = when (action) {
		is BVHActions.StartRecording -> state.copy(recording = true, recordingPath = action.path)
		is BVHActions.StopRecording -> state.copy(recording = false, recordingPath = null)
	}

	override fun observe(receiver: BVHManager) {
		var stream: BvhStream? = null

		receiver.context.state
			.map { it.recording to it.recordingPath }
			.distinctUntilChanged()
			.onEach { (recording, path) ->
				if (recording && stream == null) {
					val file = resolveBvhFile(path ?: return@onEach) ?: return@onEach
					try {
						stream = BvhStream(file).also {
							it.writeHeader(skeleton.computed.value)
							// TODO: we could write the initial T-Pose or whatever here
						}
					} catch (e: Exception) {
						AppLogger.bvh.error("Failed to start BVH recording", e)
						receiver.context.dispatch(BVHActions.StopRecording)
					}
				} else if (!recording && stream != null) {
					try {
						stream?.close()
					} catch (e: Exception) {
						AppLogger.bvh.error("Failed to finalize BVH recording", e)
					} finally {
						stream = null
					}
				}
			}.launchIn(receiver.context.scope)

		skeleton.computed.onEach { bones ->
			stream?.writeFrame(bones)
		}.launchIn(receiver.context.scope)
	}
}
