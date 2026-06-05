package dev.slimevr.updater.gui

import androidx.compose.runtime.snapshots.Snapshot
import dev.slimevr.updater.gui.UpdaterState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun UpdaterState.update(block: UpdaterState.() -> Unit) {
	withContext(Dispatchers.IO) {
		Snapshot.withMutableSnapshot {
			this@update.block()
		}
	}
}
