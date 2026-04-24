package dev.slimevr.updater

import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun UpdaterState.update(block: UpdaterState.() -> Unit) {
	withContext(Dispatchers.Main) {
		Snapshot.withMutableSnapshot {
			this@update.block()
		}
	}
}
