package dev.slimevr.bvh

import dev.slimevr.AppLogger
import dev.slimevr.config.ConfigStorage
import dev.slimevr.skeleton.Skeleton
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private suspend fun resolveBvhPath(storage: ConfigStorage, path: String): String? {
	if (path.lowercase().endsWith(".bvh")) return path
	if (!storage.ensureDirectory(path)) {
		AppLogger.bvh.error("Failed to create recording directory \"${storage.displayPath(path)}\"")
		return null
	}

	var index = 1
	while (true) {
		val candidate = "$path/BVH-Recording${index++}.bvh"
		if (!storage.exists(candidate)) return candidate
	}
}

class BVHRecordingBehaviour(
	private val skeleton: Skeleton,
	private val storage: ConfigStorage,
) : BVHBehaviourType {
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
					val resolvedPath = resolveBvhPath(storage, path ?: return@onEach) ?: return@onEach
					try {
						stream = BvhStream(storage.openTextFile(resolvedPath)).also {
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
