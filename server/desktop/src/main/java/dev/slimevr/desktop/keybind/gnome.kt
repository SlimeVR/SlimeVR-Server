package dev.slimevr.desktop.keybind

import dev.slimevr.AppLogger
import dev.slimevr.config.KeybindConfig
import dev.slimevr.desktop.install.executeShellCommand
import java.io.File

private const val GLOBAL_SHORTCUTS_PATH = "/org/gnome/settings-daemon/global-shortcuts"

private val MODIFIERS = linkedMapOf(
	"SHIFT" to "<Shift>",
	"CTRL" to "<Control>",
	"ALT" to "<Alt>",
	"SUPER" to "<Super>",
)

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
private fun toGnomeAccelerator(binding: String): String? {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	val key = parts.firstOrNull { it !in MODIFIERS } ?: return null

	val modifiers = parts.filter { it in MODIFIERS }.toSet()
	return MODIFIERS.filterKeys { it in modifiers }.values.joinToString("") + key.lowercase()
}

private fun escapeGVariant(value: String): String = value.replace("\\", "\\\\").replace("'", "\\'")

private fun buildGnomeShortcutsVariant(keybinds: List<KeybindConfig>): String = keybinds
	.mapNotNull { keybind ->
		val accelerator = toGnomeAccelerator(keybind.binding) ?: return@mapNotNull null
		"('${escapeGVariant(keybind.id.name)}', {'shortcuts': <['${escapeGVariant(accelerator)}']>, " +
			"'description': <'${escapeGVariant(keybindLabel(keybind.id))}'>})"
	}
	.joinToString(", ", prefix = "[", postfix = "]")

suspend fun gnomeShortcutsExist(appId: String): Boolean {
	val (exitCode, output) = executeShellCommand("dconf", "read", "$GLOBAL_SHORTCUTS_PATH/$appId/shortcuts") ?: return false
	return exitCode == 0 && output.isNotBlank()
}

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
