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

interface ConfigStorage {
	suspend fun read(path: String): String?
	suspend fun write(path: String, content: String)
	suspend fun backup(path: String)
	suspend fun exists(path: String): Boolean
	suspend fun ensureDirectory(path: String): Boolean
	suspend fun openTextFile(path: String): TextFileHandle
	fun displayPath(path: String): String = path
}

interface TextFileHandle {
	suspend fun write(text: String)
	suspend fun flush()
	suspend fun position(): Long
	suspend fun seek(position: Long)
	suspend fun close()
}

val jsonConfig = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
	encodeDefaults = true
}

fun configPath(vararg parts: String): String = parts.filter { it.isNotEmpty() }.joinToString("/")

suspend inline fun <reified T> loadFileWithBackup(
	storage: ConfigStorage,
	path: String,
	default: T,
	crossinline deserialize: (String) -> T,
): T = withContext(Dispatchers.IO) {
	val raw = storage.read(path)
	if (raw == null) {
		storage.write(path, jsonConfig.encodeToString(default))
		return@withContext default
	}

	try {
		deserialize(raw)
	} catch (e: Exception) {
		e.printStackTrace()
		System.err.println("Failed to load ${storage.displayPath(path)}: ${e.message}")
		try {
			storage.backup(path)
		} catch (e2: Exception) {
			System.err.println("Failed to back up corrupted file: ${e2.message}")
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
	storage: ConfigStorage,
	toPath: (S) -> String,
	serialize: (S) -> String,
): Job {
	var lastSaved = state.value
	return merge(state.debounce(500L), state.sample(2000L))
		.distinctUntilChanged()
		.filter { it != lastSaved }
		.onEach { s ->
			try {
				val path = toPath(s)
				storage.write(path, serialize(s))
				lastSaved = s
				println("Saved ${storage.displayPath(path)}")
			} catch (e: Exception) {
				System.err.println("Failed to save: ${e.message}")
			}
		}
		.launchIn(scope)
}
