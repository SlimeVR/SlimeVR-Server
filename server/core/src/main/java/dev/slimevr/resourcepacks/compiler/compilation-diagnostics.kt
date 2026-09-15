package dev.slimevr.resourcepacks.compiler

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.resourcepacks.ParsedResourcePack

data class ResourcePackCompilationDiagnostic(val packId: String, val path: String, val message: String)

class ResourcePackCompilationException(val diagnostics: List<ResourcePackCompilationDiagnostic>) :
	IllegalArgumentException(
		diagnostics.joinToString("\n") { "${it.packId}:${it.path}: ${it.message}" },
	)

internal data class ResourceOrigin(val pack: ParsedResourcePack, val path: String)

internal fun ResourceOrigin.diagnostic(message: String) = ResourcePackCompilationDiagnostic(pack.manifest.value.id, path, message)

/** Finds every cycle reachable by following [parentOf] from each of [nodes]. */
internal fun <T> reportCycles(nodes: Iterable<T>, parentOf: (T) -> T?, report: (start: T, lastVisited: T, cycle: Set<T>) -> Unit) {
	val reportedCycles = mutableSetOf<Set<T>>()
	for (node in nodes) {
		val visited = linkedSetOf<T>()
		var current: T? = node
		while (current != null && visited.add(current)) current = parentOf(current)
		if (current == null) continue
		val cycle = visited.dropWhile { it != current }.toSet()
		if (reportedCycles.add(cycle)) report(node, visited.last(), cycle)
	}
}

/** Registers [key] against [boneId] in [seen], reporting a "first used by" diagnostic through [origin] if another bone already claimed it. */
internal fun reportDuplicateBoneKey(
	seen: MutableMap<String, BoneId>,
	key: String,
	boneId: BoneId,
	label: String,
	registry: BoneRegistry,
	origin: ResourceOrigin,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	val existing = seen.putIfAbsent(key, boneId) ?: return
	val existingKey = registry.keyOf(existing) ?: existing.toString()
	diagnostics += origin.diagnostic("Duplicate $label '$key'; first used by '$existingKey'")
}
