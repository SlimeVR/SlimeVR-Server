package dev.slimevr.config

import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface ConfigStorage {
	suspend fun read(path: String): String?
	suspend fun write(path: String, content: String, overwrite: Boolean = true): Boolean
	suspend fun backup(path: String)
	suspend fun exists(path: String): Boolean
	suspend fun list(dir: String): List<String>
	suspend fun delete(path: String)

	suspend fun move(from: String, to: String)
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
		AppLogger.config.error(e, "Failed to load ${storage.displayPath(path)}")
		try {
			storage.backup(path)
		} catch (e2: Exception) {
			AppLogger.config.error(e2, "Failed to back up corrupted file")
		}
		default
	}
}

class Autosave internal constructor(
	private val job: Job,
	private val flushNow: suspend () -> Unit,
) {
	suspend fun flush() = flushNow()

	suspend fun stop() {
		flushNow()
		job.cancelAndJoin()
	}
}

/**
 * Launches a debounced autosave coroutine. Skips the initial state (already on
 * disk at start time) and any state that was already successfully persisted.
 * [Autosave.stop] writes any pending state, then start a new one to switch
 * profiles. The new autosave treats the current state as already saved.
 */
@OptIn(FlowPreview::class)
fun <S> launchAutosave(
	scope: CoroutineScope,
	state: StateFlow<S>,
	storage: ConfigStorage,
	toPath: (S) -> String,
	serialize: (S) -> String,
): Autosave {
	var lastSaved = state.value
	val writeLock = Mutex()

	suspend fun save(s: S) {
		writeLock.withLock {
			if (s == lastSaved) return
			try {
				val path = toPath(s)
				storage.write(path, serialize(s))
				lastSaved = s
				AppLogger.config.info("Saved ${storage.displayPath(path)}")
			} catch (e: Exception) {
				AppLogger.config.error(e, "Failed to save")
			}
		}
	}

	val job = merge(state.debounce(500L), state.sample(2000L))
		.distinctUntilChanged()
		.onEach { s -> save(s) }
		.launchIn(scope)
	return Autosave(job) { save(state.value) }
}
