package dev.slimevr.keybind

val MODIFIER_ORDER = listOf("CTRL", "ALT", "SHIFT", "SUPER")
private val NON_SHIFT_MODIFIERS = setOf("CTRL", "ALT", "SUPER")

private val LETTERS = ('A'..'Z').map { it.toString() }
private val DIGITS = ('0'..'9').map { it.toString() }
private val FUNCTION_KEYS = (1..24).map { "F$it" }
private val ARROW_KEYS = listOf("UP", "DOWN", "LEFT", "RIGHT")
private val NAV_KEYS = listOf("HOME", "END", "PAGE_UP", "PAGE_DOWN", "INSERT", "DELETE")
private val CONTROL_KEYS = listOf("SPACE", "ENTER", "TAB", "BACKSPACE", "ESCAPE")
private val NUMPAD_KEYS = (0..9).map { "NUMPAD_$it" } +
	listOf("NUMPAD_ADD", "NUMPAD_SUBTRACT", "NUMPAD_MULTIPLY", "NUMPAD_DIVIDE", "NUMPAD_DECIMAL", "NUMPAD_ENTER")
private val PUNCTUATION_KEYS = listOf(
	"MINUS", "EQUAL", "BRACKET_LEFT", "BRACKET_RIGHT", "BACKSLASH",
	"SEMICOLON", "QUOTE", "BACKQUOTE", "COMMA", "PERIOD", "SLASH",
)
private val SYSTEM_KEYS = listOf("PRINT_SCREEN", "PAUSE", "SCROLL_LOCK", "NUM_LOCK", "CAPS_LOCK")
val MEDIA_KEYS = listOf(
	"MEDIA_PLAY_PAUSE",
	"MEDIA_STOP",
	"MEDIA_NEXT",
	"MEDIA_PREVIOUS",
	"VOLUME_UP",
	"VOLUME_DOWN",
	"VOLUME_MUTE",
)

/** Keys meaningfully bindable without a modifier: function, media/volume and standalone system keys. */
val MODIFIER_OPTIONAL_KEYS: Set<String> =
	(FUNCTION_KEYS + MEDIA_KEYS + listOf("PRINT_SCREEN", "PAUSE", "SCROLL_LOCK")).toSet()

val KEYBIND_MAIN_KEYS: Set<String> = (
	LETTERS +
		DIGITS +
		FUNCTION_KEYS +
		ARROW_KEYS +
		NAV_KEYS +
		CONTROL_KEYS +
		NUMPAD_KEYS +
		PUNCTUATION_KEYS +
		SYSTEM_KEYS +
		MEDIA_KEYS
	).toSet()

fun canonicalKeybind(binding: String): String {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	return (MODIFIER_ORDER.filter { it in parts } + parts.filterNot { it in MODIFIER_ORDER }).joinToString("+")
}

fun isValidKeybind(binding: String): Boolean {
	val parts = binding.split('+').map { it.trim().uppercase() }.filter { it.isNotEmpty() }
	val key = parts.filterNot { it in MODIFIER_ORDER }.singleOrNull() ?: return false
	if (key !in KEYBIND_MAIN_KEYS) return false
	if (key in MODIFIER_OPTIONAL_KEYS) return true
	return parts.any { it in NON_SHIFT_MODIFIERS }
}
