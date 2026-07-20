package dev.slimevr.desktop.keybind

import com.melloware.jintellitype.JIntellitype
import dev.hannah.portals.PortalManager
import dev.hannah.portals.globalShortcuts.GlobalShortcutsHandler
import dev.hannah.portals.globalShortcuts.Shortcut
import dev.hannah.portals.globalShortcuts.ShortcutTuple
import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.config.KeybindConfig
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.ResetType

private const val KEYBIND_SOURCE = "Keybind"

private val FEET_BODY_PARTS = listOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT)

private fun currentKeybinds(appContext: AppContextProvider): List<KeybindConfig> = appContext.config.settings.context.state.value.data.keybinds

private fun bindingsFlow(appContext: AppContextProvider): Flow<List<KeybindConfig>> = appContext.config.settings.context.state
	.map { it.data.keybinds }
	.distinctUntilChangedBy { keybinds -> keybinds.map { it.id to it.binding } }

fun keybindLabel(id: KeybindId): String = id.name
	.split('_')
	.joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }

private fun toJIntellitypeAccelerator(binding: String): String = binding
	.split('+')
	.joinToString("+") { if (it == "SUPER") "WIN" else it }

private fun triggerKeybind(appContext: AppContextProvider, scope: CoroutineScope, id: KeybindId) {
	val delaySeconds = currentKeybinds(appContext).find { it.id == id }?.delay ?: 0f
	scope.safeLaunch {
		when (id) {
			KeybindId.FULL_RESET ->
				appContext.resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.FULL, delaySeconds)

			KeybindId.YAW_RESET ->
				appContext.resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.YAW, delaySeconds)

			KeybindId.MOUNTING_RESET ->
				appContext.resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.MOUNTING, delaySeconds)

			KeybindId.FEET_MOUNTING_RESET ->
				appContext.resetsManager.scheduleReset(KEYBIND_SOURCE, ResetType.MOUNTING, delaySeconds, FEET_BODY_PARTS)

			KeybindId.PAUSE_TRACKING -> {
				if (delaySeconds > 0f) delay((delaySeconds * 1000).toLong())
				val skeleton = appContext.skeleton.context
				skeleton.dispatch(SkeletonActions.PauseTracking(!skeleton.state.value.paused))
			}

			KeybindId.NONE -> Unit
		}
	}
}

suspend fun createDesktopKeybindManager(appContext: AppContextProvider, scope: CoroutineScope) {
	when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> setupWindowsKeybinds(appContext, scope)
		Platform.LINUX -> setupLinuxKeybinds(appContext, scope)
		else -> AppLogger.keybind.info("Keybinds are not supported on $CURRENT_PLATFORM")
	}
}

private suspend fun setupWindowsKeybinds(appContext: AppContextProvider, scope: CoroutineScope) {
	val instance = try {
		JIntellitype.getInstance()
	} catch (e: Throwable) {
		AppLogger.keybind.error(e, "Failed to initialize JIntellitype, keybinds will be disabled")
		return
	}

	instance.addHotKeyListener { identifier ->
		KeybindId.fromValue(identifier.toUByte())?.let { triggerKeybind(appContext, scope, it) }
	}

	var registered: List<KeybindConfig> = emptyList()
	bindingsFlow(appContext).onEach { keybinds ->
		registered.forEach { runCatching { instance.unregisterHotKey(it.id.value.toInt()) } }
		keybinds.forEach { keybind ->
			runCatching { instance.registerHotKey(keybind.id.value.toInt(), toJIntellitypeAccelerator(keybind.binding)) }
				.onFailure { AppLogger.keybind.warn("Failed to bind ${keybind.id.name} to ${keybind.binding}") }
		}
		registered = keybinds
	}.launchIn(scope)
}

private suspend fun setupLinuxKeybinds(appContext: AppContextProvider, scope: CoroutineScope) {
	var handler: GlobalShortcutsHandler? = null

	// Closing a handler disconnects its dbus connection, so every session gets its own manager
	suspend fun bind(keybinds: List<KeybindConfig>) {
		runCatching { handler?.close() }
		val shortcuts = keybinds.map { keybind ->
			ShortcutTuple(keybind.id.name, Shortcut(keybindLabel(keybind.id), keybind.binding).shortcut)
		}.toMutableList()

		handler = runCatching {
			PortalManager(SLIMEVR_IDENTIFIER).globalShortcutsRequest(shortcuts).apply {
				onShortcutActivated = { shortcutId ->
					KeybindId.entries.firstOrNull { it.name == shortcutId }?.let { triggerKeybind(appContext, scope, it) }
				}
			}
		}.onFailure {
			AppLogger.keybind.error(it, "Failed to register global shortcuts, keybinds will not work")
		}.getOrNull()
	}

	val gnomeAppId = if (isGnome()) resolveGnomeAppId() else null
	if (gnomeAppId == null) {
		// The compositor owns the triggers and we have no way to change them, so bind once and
		// leave it alone. The user rebinds from their system settings instead.
		bind(currentKeybinds(appContext))
	} else {
		// GNOME keeps the triggers in dconf and only reads them when a session is created, so a
		// change means rewriting dconf and starting a new session. Its stored values also win over
		// the ones we pass, so push the config on startup too or the gui would disagree with what
		// actually fires. The exception is a first run, where the portal seeds them and prompts.
		val needsSeeding = !gnomeShortcutsExist(gnomeAppId)

		var isInitialBind = true
		bindingsFlow(appContext).onEach { keybinds ->
			if (!(isInitialBind && needsSeeding)) {
				writeGnomeShortcuts(gnomeAppId, keybinds)
			}
			bind(keybinds)
			isInitialBind = false
		}.launchIn(scope)
	}

	scope.safeLaunch {
		try {
			awaitCancellation()
		} finally {
			runCatching { handler?.close() }
		}
	}
}
