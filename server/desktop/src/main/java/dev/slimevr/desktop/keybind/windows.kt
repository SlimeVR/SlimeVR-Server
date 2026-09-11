package dev.slimevr.desktop.keybind

import com.melloware.jintellitype.JIntellitype
import dev.slimevr.AppContextProvider
import dev.slimevr.config.KeybindConfig
import dev.slimevr.keybind.KeybindEvent
import dev.slimevr.keybind.MODIFIER_ORDER
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.rpc.KeybindId

// Win32 virtual-key codes (winuser.h). RegisterHotKey can't distinguish the numpad Enter
// from the main one, so both map to VK_RETURN.
private val WIN32_VK_CODES: Map<String, Int> = buildMap {
	('A'..'Z').forEach { put(it.toString(), it.code) }
	('0'..'9').forEach { put(it.toString(), it.code) }
	(1..24).forEach { put("F$it", 0x70 + (it - 1)) }
	put("UP", 0x26)
	put("DOWN", 0x28)
	put("LEFT", 0x25)
	put("RIGHT", 0x27)
	put("HOME", 0x24)
	put("END", 0x23)
	put("PAGE_UP", 0x21)
	put("PAGE_DOWN", 0x22)
	put("INSERT", 0x2D)
	put("DELETE", 0x2E)
	put("SPACE", 0x20)
	put("ENTER", 0x0D)
	put("TAB", 0x09)
	put("BACKSPACE", 0x08)
	put("ESCAPE", 0x1B)
	(0..9).forEach { put("NUMPAD_$it", 0x60 + it) }
	put("NUMPAD_MULTIPLY", 0x6A)
	put("NUMPAD_ADD", 0x6B)
	put("NUMPAD_SUBTRACT", 0x6D)
	put("NUMPAD_DECIMAL", 0x6E)
	put("NUMPAD_DIVIDE", 0x6F)
	put("NUMPAD_ENTER", 0x0D)
	put("MINUS", 0xBD)
	put("EQUAL", 0xBB)
	put("BRACKET_LEFT", 0xDB)
	put("BRACKET_RIGHT", 0xDD)
	put("BACKSLASH", 0xDC)
	put("SEMICOLON", 0xBA)
	put("QUOTE", 0xDE)
	put("BACKQUOTE", 0xC0)
	put("COMMA", 0xBC)
	put("PERIOD", 0xBE)
	put("SLASH", 0xBF)
	put("PRINT_SCREEN", 0x2C)
	put("PAUSE", 0x13)
	put("SCROLL_LOCK", 0x91)
	put("NUM_LOCK", 0x90)
	put("CAPS_LOCK", 0x14)
	put("MEDIA_PLAY_PAUSE", 0xB3)
	put("MEDIA_STOP", 0xB2)
	put("MEDIA_NEXT", 0xB0)
	put("MEDIA_PREVIOUS", 0xB1)
	put("VOLUME_UP", 0xAF)
	put("VOLUME_DOWN", 0xAE)
	put("VOLUME_MUTE", 0xAD)
}

private fun win32Modifiers(parts: List<String>): Int {
	var mask = 0
	if ("ALT" in parts) mask = mask or JIntellitype.MOD_ALT
	if ("CTRL" in parts) mask = mask or JIntellitype.MOD_CONTROL
	if ("SHIFT" in parts) mask = mask or JIntellitype.MOD_SHIFT
	if ("SUPER" in parts) mask = mask or JIntellitype.MOD_WIN
	return mask
}

/** Returns the (modifier mask, virtual-key code) pair for [RegisterHotKey](https://learn.microsoft.com/windows/win32/api/winuser/nf-winuser-registerhotkey), or null if the key has no Windows mapping. */
private fun toWindowsHotkey(binding: String): Pair<Int, Int>? {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	val key = parts.filterNot { it in MODIFIER_ORDER }.singleOrNull() ?: return null
	val vkCode = WIN32_VK_CODES[key] ?: return null
	return win32Modifiers(parts) to vkCode
}

suspend fun setupWindowsKeybinds(appContext: AppContextProvider, scope: CoroutineScope) {
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

	val applyMutex = Mutex()
	var registered: List<KeybindConfig> = emptyList()
	suspend fun apply(keybinds: List<KeybindConfig>) = applyMutex.withLock {
		registered.forEach { runCatching { instance.unregisterHotKey(it.id.value.toInt()) } }
		registered = emptyList()

		val active = keybinds.filter { it.binding.isNotEmpty() }
		active.forEach { keybind ->
			val hotkey = toWindowsHotkey(keybind.binding)
			if (hotkey == null) {
				AppLogger.keybind.warn("No Windows key mapping for ${keybind.id.name} binding ${keybind.binding}")
				return@forEach
			}
			val (mask, vkCode) = hotkey
			runCatching { instance.registerHotKey(keybind.id.value.toInt(), mask, vkCode) }
				.onFailure { AppLogger.keybind.warn("Failed to bind ${keybind.id.name} to ${keybind.binding}") }
		}
		registered = active
	}

	apply(appContext.config.settings.context.state.value.data.keybinds)
	appContext.keybindManager.events.on<KeybindEvent.Rebind> { apply(it.keybinds) }.launchIn(scope)
}
