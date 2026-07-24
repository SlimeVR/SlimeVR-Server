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
import dev.slimevr.keybind.KeybindEvent
import kotlinx.coroutines.CoroutineScope
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
import solarxr_protocol.rpc.KeybindId

private const val BINDING_SETTLE_MS = 300L

fun keybindLabel(id: KeybindId): String = id.name
	.split('_')
	.joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.uppercase() } }

private fun toJIntellitypeAccelerator(binding: String): String = binding
	.split('+')
	.joinToString("+") { if (it == "SUPER") "WIN" else it }

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
		KeybindId.fromValue(identifier.toUByte())?.let { id ->
			scope.launch { appContext.keybindManager.events.emit(KeybindEvent.Fired(id)) }
		}
	}

	var registered: List<KeybindConfig> = emptyList()
	suspend fun apply(keybinds: List<KeybindConfig>) {
		registered.forEach { runCatching { instance.unregisterHotKey(it.id.value.toInt()) } }
		registered = emptyList()
		if (appContext.keybindManager.recording) return

		val active = keybinds.filter { it.binding.isNotEmpty() }
		active.forEach { keybind ->
			runCatching { instance.registerHotKey(keybind.id.value.toInt(), toJIntellitypeAccelerator(keybind.binding)) }
				.onFailure { AppLogger.keybind.warn("Failed to bind ${keybind.id.name} to ${keybind.binding}") }
		}
		registered = active
	}

	apply(appContext.config.settings.context.state.value.data.keybinds)
	appContext.keybindManager.events.on<KeybindEvent.Rebind> { apply(it.keybinds) }.launchIn(scope)

	appContext.keybindManager.context.state
		.map { it.recording }
		.distinctUntilChanged()
		.onEach { apply(appContext.config.settings.context.state.value.data.keybinds) }
		.launchIn(scope)
}

@OptIn(FlowPreview::class)
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
		handler = null
		if (appContext.keybindManager.recording) return

		val shortcuts = keybinds
			.filter { it.binding.isNotEmpty() }
			.map { keybind -> ShortcutTuple(keybind.id.name, Shortcut(keybindLabel(keybind.id), keybind.binding).shortcut) }
			.toMutableList()
		if (shortcuts.isEmpty()) return

		handler = runCatching {
			PortalManager(SLIMEVR_IDENTIFIER).globalShortcutsRequest(shortcuts).apply {
				onShortcutActivated = { shortcutId ->
					KeybindId.entries.firstOrNull { it.name == shortcutId }?.let { id ->
						scope.launch { appContext.keybindManager.events.emit(KeybindEvent.Fired(id)) }
					}
				}
			}
		}.onFailure {
			AppLogger.keybind.error(it, "Failed to register global shortcuts, keybinds will not work")
		}.getOrNull()
	}
	if (gnomeAppId != null) {
		adoptGnomeShortcuts(gnomeAppId)
	}

	bind(appContext.config.settings.context.state.value.data.keybinds)

	appContext.keybindManager.context.state
		.map { it.recording }
		.distinctUntilChanged()
		.filter { it }
		.onEach {
			runCatching { handler?.close() }
			handler = null
		}
		.launchIn(scope)

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
