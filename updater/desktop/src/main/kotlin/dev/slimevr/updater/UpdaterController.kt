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
import java.io.File
import kotlin.system.exitProcess

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
			UpdaterScreen(state, updaterIO, { launchServer() })
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

	fun launchServer() {
		TerminalUtil.info("Attempting to launch SlimeVR server")
		when (os.currentPlatform) {
			OperatingSystem.WINDOWS -> {
				val exeFile = File("SlimeVR.exe").absoluteFile
				if (exeFile.exists()) {
					launchDetached(listOf(exeFile.absolutePath))
				} else {
					TerminalUtil.error("SlimeVR.exe not found at ${exeFile.absolutePath}")
				}

			}

			OperatingSystem.LINUX -> {
				launchDetached(listOf("./SlimeVR-amd64.appimage"))
			}

			else -> {
				TerminalUtil.error("Unknown operating system")
				return
			}
		}
		exitProcess(0)
	}
	fun launchDetached(command: List<String>) {
		val processBuilder = ProcessBuilder(command)
		processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD)
		processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD)

		try {
			processBuilder.start()
			println("Process started successfully. Closing parent...")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}
