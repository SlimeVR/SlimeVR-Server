package dev.slimevr.updater

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdaterController {

	val state = UpdaterState()
	val updaterIO = UpdaterIO(state)

	fun startGui() = application {
		val scope = CoroutineScope(Dispatchers.Default)
		Window(
			onCloseRequest = ::exitApplication,
			undecorated = true,
			transparent = true,
			state = WindowState(width = 400.dp, height = 450.dp, position = WindowPosition(Alignment.Center)),
		) {
			UpdaterScreen(state, updaterIO)
			scope.launch {
				runUpdate()
			}
		}
	}

	fun startHeadless() {
		val scope = CoroutineScope(Dispatchers.Default)
		scope.launch {
			runHeadless()
		}
	}

	private suspend fun runUpdate() {
		println("run update")
		val updater = Updater(state, updaterIO)
		updater.runUpdater()
	}

	private suspend fun runHeadless() {
	}
}
