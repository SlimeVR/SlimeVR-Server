package dev.slimevr.desktop.keybind

import dev.hannah.portals.PortalManager
import dev.hannah.portals.globalShortcuts.GlobalShortcutsHandler
import dev.hannah.portals.globalShortcuts.Shortcut
import dev.hannah.portals.globalShortcuts.ShortcutTuple
import dev.slimevr.AppContextProvider
import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.config.KeybindConfig
import dev.slimevr.config.SettingsActions
import dev.slimevr.keybind.KeybindEvent
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import solarxr_protocol.rpc.KeybindId

private const val BINDING_SETTLE_MS = 300L
private const val PORTAL_BIND_TIMEOUT_MS = 5000L

@OptIn(FlowPreview::class)
suspend fun setupLinuxKeybinds(appContext: AppContextProvider, scope: CoroutineScope) {
	val gnomeAppId = if (isGnome()) resolveGnomeAppId() else null
	var handler: GlobalShortcutsHandler? = null
	val bindMutex = Mutex()

	suspend fun adoptGnomeShortcuts(appId: String) {
		val stored = readGnomeShortcuts(appId).ifEmpty { return }
		appContext.config.settings.context.dispatch(
			SettingsActions.Update {
				copy(keybinds = keybinds.map { keybind -> stored[keybind.id]?.let { keybind.copy(binding = it) } ?: keybind })
			},
		)
	}

	// GNOME rejects a second session for the same app_id while one is still open, so the old
	// handler always closes before a new one is requested. CreateSession has no timeout of its
	// own, so a failed rebind falls back to the last keybinds known to work.
	var lastGoodKeybinds: List<KeybindConfig> = emptyList()

	suspend fun attemptBind(keybinds: List<KeybindConfig>): GlobalShortcutsHandler? {
		val shortcuts = keybinds
			.filter { it.binding.isNotEmpty() }
			.map { keybind ->
				val trigger = toGnomeAccelerator(keybind.binding) ?: keybind.binding
				ShortcutTuple(keybind.id.name, Shortcut(keybindLabel(keybind.id), trigger).shortcut)
			}
			.toMutableList()
		if (shortcuts.isEmpty()) return null

		return runCatching {
			withTimeout(PORTAL_BIND_TIMEOUT_MS) {
				runInterruptible(Dispatchers.IO) {
					PortalManager(SLIMEVR_IDENTIFIER).globalShortcutsRequest(shortcuts)
				}
			}
		}.getOrNull()?.apply {
			onShortcutActivated = { shortcutId ->
				KeybindId.entries.firstOrNull { it.name == shortcutId }?.let { id ->
					scope.launch { appContext.keybindManager.events.emit(KeybindEvent.Fired(id)) }
				}
			}
		}
	}

	suspend fun bind(keybinds: List<KeybindConfig>) = bindMutex.withLock {
		runCatching { handler?.close() }
		handler = null

		if (keybinds.none { it.binding.isNotEmpty() }) {
			lastGoodKeybinds = keybinds
			return@withLock
		}

		handler = attemptBind(keybinds)
		if (handler != null) {
			lastGoodKeybinds = keybinds
			return@withLock
		}

		AppLogger.keybind.error("Failed to register global shortcuts for $keybinds, falling back to the last working bindings")
		if (keybinds !== lastGoodKeybinds) handler = attemptBind(lastGoodKeybinds)
	}
	if (gnomeAppId != null) {
		adoptGnomeShortcuts(gnomeAppId)
	}

	bind(appContext.config.settings.context.state.value.data.keybinds)

	combine(
		appContext.keybindManager.context.state.map { it.recording }.distinctUntilChanged(),
		appContext.config.settings.context.state.map { it.data.keybinds }.distinctUntilChangedBy { keybinds -> keybinds.map { it.id to it.binding } },
	) { recording, keybinds -> recording to keybinds }
		.drop(1)
		.debounce(BINDING_SETTLE_MS)
		.filter { (recording, _) -> !recording }
		.onEach { (_, keybinds) ->
			if (gnomeAppId != null) writeGnomeShortcuts(gnomeAppId, keybinds)
			bind(keybinds)
		}
		.launchIn(scope)

	scope.launch {
		try {
			awaitCancellation()
		} finally {
			runCatching { handler?.close() }
		}
	}
}
