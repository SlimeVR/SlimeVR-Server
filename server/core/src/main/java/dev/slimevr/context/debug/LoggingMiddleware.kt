package dev.slimevr.context.debug

import dev.slimevr.context.Context
import kotlin.reflect.KClass

private object Ansi {
	const val RESET = "\u001B[0m"
	const val DIM = "\u001B[2m"
	const val CYAN = "\u001B[36m"
	const val YELLOW = "\u001B[33m"
	const val GREEN = "\u001B[32m"
	const val RED = "\u001B[31m"
	const val MAGENTA = "\u001B[35m"
	const val ARROW = "\u001B[2m→\u001B[0m"
}

enum class DiffStyle { INLINE, MULTILINE }

class LoggingMiddleware<S, A>(
	private val logNoOps: Boolean = false,
	private val diffStyle: DiffStyle = DiffStyle.MULTILINE,
	private val allow: Set<KClass<*>>? = null,
	private val block: Set<KClass<*>> = emptySet(),
) : DebugMiddleware<S, A> {
	private var contextName = ""
	private val tag get() = "${Ansi.CYAN}[$contextName]${Ansi.RESET}"

	override fun init(context: Context<S, A>) {
		contextName = context.name
	}

	private fun isAllowed(action: A): Boolean {
		val klass = action!!::class
		if (klass in block) return false
		if (allow != null && klass !in allow) return false
		return true
	}

	override fun onDispatch(caller: String?, before: S, action: A, after: S) {
		if (!isAllowed(action)) return
		if (!logNoOps && before == after) return
		val actionName = "${Ansi.YELLOW}${action!!::class.simpleName ?: "UnknownAction"}${Ansi.RESET}"
		val callerSuffix = if (caller != null) " ${Ansi.DIM}(from $caller)${Ansi.RESET}" else ""
		println(formatLine("$tag $actionName$callerSuffix", before, after))
	}

	override fun onDispatchAll(caller: String?, before: S, actions: List<A>, after: S) {
		val visible = actions.filter { action -> isAllowed(action) }
		if (visible.isEmpty()) return
		if (!logNoOps && before == after) return
		val names = visible.joinToString(", ") { action -> action!!::class.simpleName ?: "?" }
		val actionName = "${Ansi.MAGENTA}batch${Ansi.RESET} ${Ansi.YELLOW}[$names]${Ansi.RESET}"
		val callerSuffix = if (caller != null) " ${Ansi.DIM}(from $caller)${Ansi.RESET}" else ""
		println(formatLine("$tag $actionName$callerSuffix", before, after))
	}

	private fun formatLine(header: String, before: S, after: S): String {
		if (before == after) return "$header ${Ansi.DIM}(no-op)${Ansi.RESET}"
		val changes = fieldChanges(before.toString(), after.toString())
		if (changes.isEmpty()) return "$header ${Ansi.DIM}(no-op)${Ansi.RESET}"
		if (diffStyle == DiffStyle.INLINE) {
			val inline = changes.joinToString(", ") { c ->
				"${Ansi.DIM}${c.name}:${Ansi.RESET} ${Ansi.RED}${c.before}${Ansi.RESET} ${Ansi.ARROW} ${Ansi.GREEN}${c.after}${Ansi.RESET}"
			}
			return "$header $inline"
		}
		if (changes.size == 1 && !isNestedObject(changes[0].before)) {
			val c = changes[0]
			return "$header ${Ansi.DIM}${c.name}:${Ansi.RESET} ${Ansi.RED}${c.before}${Ansi.RESET} ${Ansi.ARROW} ${Ansi.GREEN}${c.after}${Ansi.RESET}"
		}
		val nameWidth = changes.maxOf { c -> c.name.length }
		val rows = changes.joinToString("\n") { c -> formatChange(c, nameWidth) }
		return "$header\n$rows"
	}
}

private fun isNestedObject(value: String): Boolean = value.contains('(') && value.endsWith(')')

private fun formatChange(c: FieldChange, nameWidth: Int): String {
	if (isNestedObject(c.before) && isNestedObject(c.after)) {
		val subChanges = fieldChanges(c.before, c.after)
		if (subChanges.isNotEmpty()) {
			val subNameWidth = subChanges.maxOf { s -> s.name.length }
			val subOldWidth = subChanges.maxOf { s -> s.before.length }
			val rows = subChanges.joinToString("\n") { s ->
				val name = s.name.padEnd(subNameWidth)
				val old = s.before.padEnd(subOldWidth)
				"    ${Ansi.DIM}$name${Ansi.RESET}  ${Ansi.RED}$old${Ansi.RESET}  ${Ansi.ARROW}  ${Ansi.GREEN}${s.after}${Ansi.RESET}"
			}
			val typeName = c.before.substringBefore('(')
			return "  ${Ansi.DIM}${c.name.padEnd(nameWidth)}${Ansi.RESET}  ${Ansi.DIM}$typeName${Ansi.RESET}\n$rows"
		}
	}
	return "  ${Ansi.DIM}${c.name.padEnd(nameWidth)}${Ansi.RESET}  ${Ansi.RED}${c.before}${Ansi.RESET}  ${Ansi.ARROW}  ${Ansi.GREEN}${c.after}${Ansi.RESET}"
}

private data class FieldChange(val name: String, val before: String, val after: String)

private fun parseFields(s: String): Map<String, String> {
	val inner = s.substringAfter('(').dropLast(1)
	val fields = mutableListOf<String>()
	var depth = 0
	var start = 0
	for (i in inner.indices) {
		when (inner[i]) {
			'(' -> depth++

			')' -> depth--

			',' -> if (depth == 0) {
				fields.add(inner.substring(start, i).trim())
				start = i + 1
			}
		}
	}
	if (start < inner.length) fields.add(inner.substring(start).trim())
	return fields.associate { field ->
		val eq = field.indexOf('=')
		field.substring(0, eq) to field.substring(eq + 1)
	}
}

private fun fieldChanges(before: String, after: String): List<FieldChange> {
	val beforeMap = parseFields(before)
	val afterMap = parseFields(after)
	return beforeMap.keys.intersect(afterMap.keys)
		.filter { key -> beforeMap[key] != afterMap[key] }
		.map { key -> FieldChange(key, beforeMap[key]!!, afterMap[key]!!) }
}
