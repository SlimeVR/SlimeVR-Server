package dev.slimevr.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

val jsonConfig = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
	encodeDefaults = true
}

suspend fun atomicWriteFile(file: File, content: String) = withContext(Dispatchers.IO) {
	file.parentFile?.mkdirs()
	val tmp = File(file.parent, "${file.name}.tmp")
	tmp.writeText(content)
	Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
	Unit
}

suspend inline fun <reified T> loadFileWithBackup(file: File, default: T, crossinline deserialize: (String) -> T): T =
	withContext(Dispatchers.IO) {
		if (!file.exists()) {
			atomicWriteFile(file, jsonConfig.encodeToString(default))
			return@withContext default
		}

		try {
			deserialize(file.readText())
		} catch (e: Exception) {
			System.err.println("Failed to load ${file.absolutePath}: ${e.message}")
			if (file.exists()) {
				try {
					val bakTmp = File(file.parent, "${file.name}.bak.tmp")
					file.copyTo(bakTmp, overwrite = true)
					Files.move(
						bakTmp.toPath(),
						File(file.parent, "${file.name}.bak").toPath(),
						StandardCopyOption.ATOMIC_MOVE,
						StandardCopyOption.REPLACE_EXISTING
					)
				} catch (e2: Exception) {
					System.err.println("Failed to back up corrupted file: ${e2.message}")
				}
			}
			default
		}
	}

/**
 * Launches a debounced autosave coroutine. Skips the initial state (already on
 * disk at start time) and any state that was already successfully persisted.
 * Cancel and restart to switch profiles. the new job treats the current state
 * as already saved.
 */
@OptIn(FlowPreview::class)
fun <S> launchAutosave(
	scope: CoroutineScope,
	state: StateFlow<S>,
	toFile: (S) -> File,
	serialize: (S) -> String,
): Job {
	var lastSaved = state.value
	return merge(state.debounce(500L), state.sample(2000L))
		.distinctUntilChanged()
		.filter { it != lastSaved }
		.onEach { s ->
			try {
				val file = toFile(s)
				atomicWriteFile(file, serialize(s))
				lastSaved = s
				println("Saved ${file.absolutePath}")
			} catch (e: Exception) {
				System.err.println("Failed to save: ${e.message}")
			}
		}
		.launchIn(scope)
}
