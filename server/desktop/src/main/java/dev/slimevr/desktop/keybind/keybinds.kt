package dev.slimevr.desktop.keybind

import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.rpc.KeybindId

fun keybindLabel(id: KeybindId): String = id.name
	.split('_')
	.joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }

suspend fun createDesktopKeybindManager(appContext: AppContextProvider, scope: CoroutineScope) {
	when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> setupWindowsKeybinds(appContext, scope)
		Platform.LINUX -> setupLinuxKeybinds(appContext, scope)
		else -> AppLogger.keybind.info("Keybinds are not supported on $CURRENT_PLATFORM")
	}
}
