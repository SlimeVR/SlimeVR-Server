package dev.slimevr.desktop.serial

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.StandardWatchEventKinds.OVERFLOW

private const val TTY_PREFIX = "tty"

fun createLinuxPortChanges(): Flow<Unit> = callbackFlow {
	val watchService = withContext(Dispatchers.IO) {
		FileSystems.getDefault().newWatchService().also { Path.of("/dev").register(it, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY) }
	}

	launch(Dispatchers.IO) {
		try {
			while (true) {
				val key = watchService.take()
				// An overflow means events were lost, so anything may have changed
				val ttyChanged = key.pollEvents().any {
					it.kind() == OVERFLOW || (it.context() as? Path)?.toString()?.startsWith(TTY_PREFIX) == true
				}
				if (ttyChanged) trySend(Unit)
				if (!key.reset()) break
			}
		} catch (_: ClosedWatchServiceException) {
			// Closed by awaitClose
		}
	}

	awaitClose { watchService.close() }
}
