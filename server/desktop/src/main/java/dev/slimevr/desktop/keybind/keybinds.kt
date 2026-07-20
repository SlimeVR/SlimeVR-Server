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
import dev.slimevr.config.SettingsActions
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
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

// The gui sends one request per keybind, so changing several at once (or resetting them all) arrives
// as a burst. Recreating a portal session for each one makes GNOME reject the bind and leaves
// nothing grabbed, so settle first and apply the burst as a single change.
private const val BINDING_SETTLE_MS = 300L

@OptIn(FlowPreview::class)
private fun bindingsFlow(appContext: AppContextProvider): Flow<List<KeybindConfig>> = appContext.config.settings.context.state
	.map { it.data.keybinds }
	.distinctUntilChangedBy { keybinds -> keybinds.map { it.id to it.binding } }
	.debounce(BINDING_SETTLE_MS)

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
	val gnomeAppId = if (isGnome()) resolveGnomeAppId() else null
	var handler: GlobalShortcutsHandler? = null

	suspend fun adoptGnomeShortcuts(appId: String) {
		val stored = readGnomeShortcuts(appId).ifEmpty { return }
		appContext.config.settings.context.dispatch(
			SettingsActions.Update {
				copy(keybinds = keybinds.map { keybind -> stored[keybind.id]?.let { keybind.copy(binding = it) } ?: keybind })
			},
		)
	}

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
				if (gnomeAppId != null) {
					onShortcutsChanged = { scope.safeLaunch { adoptGnomeShortcuts(gnomeAppId) } }
				}
			}
		}.onFailure {
			AppLogger.keybind.error(it, "Failed to register global shortcuts, keybinds will not work")
		}.getOrNull()
	}

	bind(currentKeybinds(appContext))

	if (gnomeAppId != null) {
		adoptGnomeShortcuts(gnomeAppId)

		bindingsFlow(appContext)
			.onEach { keybinds ->
				writeGnomeShortcuts(gnomeAppId, keybinds)
				bind(keybinds)
			}
			.launchIn(scope)
	}

	scope.safeLaunch {
		try {
			awaitCancellation()
		} finally {
			runCatching { handler?.close() }
		}
	}
}
