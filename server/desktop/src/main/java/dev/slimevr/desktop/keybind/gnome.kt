package dev.slimevr.desktop.keybind

import dev.slimevr.config.KeybindConfig
import dev.slimevr.desktop.install.executeShellCommand
import dev.slimevr.keybind.canonicalKeybind
import dev.slimevr.logging.AppLogger
import solarxr_protocol.rpc.KeybindId
import java.io.File

private const val GLOBAL_SHORTCUTS_PATH = "/org/gnome/settings-daemon/global-shortcuts"

private val MODIFIERS = linkedMapOf(
	"SHIFT" to "<Shift>",
	"CTRL" to "<Control>",
	"ALT" to "<Alt>",
	"SUPER" to "<Super>",
)

// GTK keyval names (gtk_accelerator_name), used both for GNOME's own dconf-stored
// accelerators and as the preferred_trigger the xdg-desktop-portal GlobalShortcuts
// implementation parses with gtk_accelerator_parse.
private val KEY_TO_GTK: Map<String, String> = buildMap {
	('A'..'Z').forEach { put(it.toString(), it.lowercaseChar().toString()) }
	('0'..'9').forEach { put(it.toString(), it.toString()) }
	(1..24).forEach { put("F$it", "F$it") }
	put("UP", "Up")
	put("DOWN", "Down")
	put("LEFT", "Left")
	put("RIGHT", "Right")
	put("HOME", "Home")
	put("END", "End")
	put("PAGE_UP", "Page_Up")
	put("PAGE_DOWN", "Page_Down")
	put("INSERT", "Insert")
	put("DELETE", "Delete")
	put("SPACE", "space")
	put("ENTER", "Return")
	put("TAB", "Tab")
	put("BACKSPACE", "BackSpace")
	put("ESCAPE", "Escape")
	(0..9).forEach { put("NUMPAD_$it", "KP_$it") }
	put("NUMPAD_ADD", "KP_Add")
	put("NUMPAD_SUBTRACT", "KP_Subtract")
	put("NUMPAD_MULTIPLY", "KP_Multiply")
	put("NUMPAD_DIVIDE", "KP_Divide")
	put("NUMPAD_DECIMAL", "KP_Decimal")
	put("NUMPAD_ENTER", "KP_Enter")
	put("MINUS", "minus")
	put("EQUAL", "equal")
	put("BRACKET_LEFT", "bracketleft")
	put("BRACKET_RIGHT", "bracketright")
	put("BACKSLASH", "backslash")
	put("SEMICOLON", "semicolon")
	put("QUOTE", "apostrophe")
	put("BACKQUOTE", "grave")
	put("COMMA", "comma")
	put("PERIOD", "period")
	put("SLASH", "slash")
	put("PRINT_SCREEN", "Print")
	put("PAUSE", "Pause")
	put("SCROLL_LOCK", "Scroll_Lock")
	put("NUM_LOCK", "Num_Lock")
	put("CAPS_LOCK", "Caps_Lock")
	put("MEDIA_PLAY_PAUSE", "AudioPlay")
	put("MEDIA_STOP", "AudioStop")
	put("MEDIA_NEXT", "AudioNext")
	put("MEDIA_PREVIOUS", "AudioPrev")
	put("VOLUME_UP", "AudioRaiseVolume")
	put("VOLUME_DOWN", "AudioLowerVolume")
	put("VOLUME_MUTE", "AudioMute")
}
private val GTK_TO_KEY: Map<String, String> = KEY_TO_GTK.entries.associate { (k, v) -> v to k }

fun isGnome(): Boolean = System.getenv("XDG_CURRENT_DESKTOP")?.uppercase()?.contains("GNOME") == true

fun resolveGnomeAppId(): String? {
	val cgroup = runCatching { File("/proc/self/cgroup").readText() }.getOrNull() ?: return null

	val scope = cgroup.lineSequence()
		.map { line -> line.substringAfterLast('/', "") }
		.firstOrNull { it.startsWith("app-") && it.endsWith(".scope") }
		?: return null

	// app-<id>-<instance>.scope, where instance is the pid that launched it
	return scope
		.removePrefix("app-")
		.removeSuffix(".scope")
		.substringBeforeLast('-')
		.ifEmpty { null }
}

/** Converts our `CTRL+ALT+SHIFT+Y` form into GTK's `<Shift><Control><Alt>y` form. */
fun toGnomeAccelerator(binding: String): String? {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	val key = parts.firstOrNull { it !in MODIFIERS } ?: return null
	val gtkKey = KEY_TO_GTK[key] ?: return null

	val modifiers = parts.filter { it in MODIFIERS }.toSet()
	return MODIFIERS.filterKeys { it in modifiers }.values.joinToString("") + gtkKey
}

private fun fromGnomeAccelerator(accelerator: String): String? {
	val gtkKey = accelerator.substringAfterLast('>').ifEmpty { return null }
	val key = GTK_TO_KEY[gtkKey] ?: return null
	val modifiers = MODIFIERS.filterValues { it in accelerator }.keys
	return canonicalKeybind((modifiers + key).joinToString("+"))
}

private val SHORTCUT_ENTRY = Regex("""\('([A-Z_]+)',\s*\{'shortcuts':\s*<\['([^']*)'\]>""")

suspend fun readGnomeShortcuts(appId: String): Map<KeybindId, String> {
	val (exitCode, output) = executeShellCommand("dconf", "read", "$GLOBAL_SHORTCUTS_PATH/$appId/shortcuts")
		?: return emptyMap()
	if (exitCode != 0) return emptyMap()

	return SHORTCUT_ENTRY.findAll(output).mapNotNull { match ->
		val (name, accelerator) = match.destructured
		val id = KeybindId.entries.firstOrNull { it.name == name } ?: return@mapNotNull null
		val binding = fromGnomeAccelerator(accelerator) ?: return@mapNotNull null
		id to binding
	}.toMap()
}

private fun escapeGVariant(value: String): String = value.replace("\\", "\\\\").replace("'", "\\'")

private fun buildGnomeShortcutsVariant(keybinds: List<KeybindConfig>): String = keybinds
	.mapNotNull { keybind ->
		val accelerator = toGnomeAccelerator(keybind.binding) ?: return@mapNotNull null
		"('${escapeGVariant(keybind.id.name)}', {'shortcuts': <['${escapeGVariant(accelerator)}']>, " +
			"'description': <'${escapeGVariant(keybindLabel(keybind.id))}'>})"
	}
	.joinToString(", ", prefix = "[", postfix = "]")

suspend fun writeGnomeShortcuts(appId: String, keybinds: List<KeybindConfig>): Boolean {
	val bound = keybinds.filter { toGnomeAccelerator(it.binding) != null }
	if (bound.isEmpty()) return false

	val variant = buildGnomeShortcutsVariant(bound)
	val (exitCode, output) = executeShellCommand("dconf", "write", "$GLOBAL_SHORTCUTS_PATH/$appId/shortcuts", variant)
		?: return false

	if (exitCode != 0) {
		AppLogger.keybind.warn("Failed to write GNOME shortcuts for $appId: ${output.trim()}")
		return false
	}
	return true
}
