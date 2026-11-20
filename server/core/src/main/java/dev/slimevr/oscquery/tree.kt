package dev.slimevr.oscquery

class OscQueryTree(nodes: Iterable<OscQueryNode> = emptyList()) {
	private val nodesByPath = mutableMapOf<String, OscQueryNode>()

	init {
		nodesByPath["/"] = OscQueryNode(fullPath = "/", contents = emptyMap())
		nodes.forEach(::add)
	}

	fun add(node: OscQueryNode) {
		require(node.fullPath.startsWith("/")) { "OSCQuery node path must start with '/': ${node.fullPath}" }
		require(node.fullPath.length == 1 || !node.fullPath.endsWith("/")) {
			"OSCQuery node path must not end with '/': ${node.fullPath}"
		}

		ensureParent(node.parentPath)
		nodesByPath[node.fullPath] = node
		rebuildContents()
	}

	fun remove(path: String): Boolean {
		val normalized = normalizePath(path)
		if (normalized == "/") return false
		val removed = nodesByPath.remove(normalized) != null
		if (removed) {
			val prefix = "$normalized/"
			nodesByPath.keys.filter { it.startsWith(prefix) }.forEach { nodesByPath.remove(it) }
			rebuildContents()
		}
		return removed
	}

	fun find(path: String): OscQueryNode? = nodesByPath[normalizePath(path)]

	private fun ensureParent(path: String) {
		if (nodesByPath.containsKey(path)) return
		if (path != "/") ensureParent(OscQueryNode(path).parentPath)
		nodesByPath[path] = OscQueryNode(fullPath = path)
	}

	private fun rebuildContents() {
		val source = nodesByPath.toMap()

		fun rebuildNode(path: String): OscQueryNode {
			val node = source.getValue(path)
			val children = source.values
				.filter { it.fullPath != path && it.parentPath == path }
				.sortedBy { it.name }
				.associate { it.name to rebuildNode(it.fullPath) }
			return node.copy(contents = children.ifEmpty { null })
		}

		val rebuilt = source.keys.associateWith(::rebuildNode)
		nodesByPath.clear()
		nodesByPath.putAll(rebuilt)
	}
}

internal fun normalizePath(path: String): String {
	val withoutQuery = path.substringBefore('?')
	val prefixed = if (withoutQuery.startsWith('/')) withoutQuery else "/$withoutQuery"
	return prefixed.trimEnd('/').ifEmpty { "/" }
}
