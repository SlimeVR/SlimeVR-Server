package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.compiler.ResourceOrigin
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationDiagnostic
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.resourcepacks.compiler.diagnostic
import java.util.PriorityQueue

/** Stable ordering of the packs. core, manifest constraints, then pack ID */
internal fun orderResourcePacks(catalog: ResourcePackCatalog): ResourcePackCatalog {
	val packs = listOf(catalog.core) + catalog.userPacks
	val byId = packs.associateBy { it.manifest.value.id }
	val diagnostics = mutableListOf<ResourcePackCompilationDiagnostic>()
	for ((id, duplicates) in packs.groupBy { it.manifest.value.id }) {
		if (duplicates.size > 1) {
			for (pack in duplicates) diagnostics += ResourceOrigin(pack, pack.manifest.path).diagnostic("Duplicate pack ID '$id'")
		}
	}
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)
	val edges = byId.keys.associateWith { mutableSetOf<String>() }
	val incoming = byId.keys.associateWith { 0 }.toMutableMap()
	fun edge(before: String, after: String) {
		if (before !in byId || after !in byId) return
		if (edges.getValue(before).add(after)) incoming[after] = incoming.getValue(after) + 1
	}
	for (pack in catalog.userPacks) edge(catalog.core.manifest.value.id, pack.manifest.value.id)
	for (pack in packs) {
		val manifest = pack.manifest.value
		for (before in manifest.before) edge(manifest.id, before)
		for (after in manifest.after) edge(after, manifest.id)
	}
	val ready = PriorityQueue(incoming.filterValues { it == 0 }.keys)
	val ordered = mutableListOf<ParsedResourcePack>()
	while (ready.isNotEmpty()) {
		val id = ready.remove()
		ordered += byId.getValue(id)
		for (next in edges.getValue(id)) {
			incoming[next] = incoming.getValue(next) - 1
			if (incoming[next] == 0) ready.add(next)
		}
	}
	if (ordered.size != packs.size) {
		val blocked = incoming.filterValues { it > 0 }.keys.sorted()
		throw ResourcePackCompilationException(
			blocked.map { id ->
				val pack = byId.getValue(id)
				ResourceOrigin(pack, pack.manifest.path).diagnostic("Pack ordering cycle blocks: ${blocked.joinToString()}; core must load first")
			},
		)
	}
	return catalog.copy(userPacks = ordered.filter { it !== catalog.core })
}
