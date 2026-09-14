package dev.slimevr.bones

import solarxr_protocol.connection.BoneDefinition
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

private val BODY_PART_BY_VALUE: Array<BodyPart?> = arrayOfNulls<BodyPart>(UByte.MAX_VALUE.toInt() + 1).also { array ->
	for (bodyPart in BodyPart.entries) array[bodyPart.value.toInt()] = bodyPart
}

/**
 * The server's bone identity and hierarchy. IDs are dense (`1..maxId`),
 * so [BoneMap]/[BoneSet] can use a plain array instead of a hash table.
 *
 * [BodyPart] is only used at the edges: to seed [standard], and to resolve specific bones by
 * name. Everything else is [BoneId]-keyed, so a bone with no standard body part still works.
 */
class BoneRegistry private constructor(
	val value: WireBoneRegistry,
	val maxId: Int,
	val root: BoneId,
	private val byId: Array<BoneDefinition?>,
	private val byKey: Map<String, BoneDefinition>,
	private val childIds: Array<IntArray>,
) {
	operator fun get(id: BoneId): BoneDefinition? = byId.getOrNull(id.value.toInt())

	/** Constant-time lookup with no per-call allocation. */
	operator fun get(bodyPart: BodyPart): BoneId? {
		if (bodyPart == BodyPart.NONE) return null
		val id = BoneId(bodyPart.value.toUShort())
		return id.takeIf { this[it] != null }
	}
	fun byKey(key: String): BoneDefinition? = byKey[key]

	/** The registry key is stable across a registry rebuild; a [BoneId] is not. */
	operator fun get(key: String): BoneId? = byKey(key)?.let { BoneId(it.id) }

	/** Inverse of `get(BodyPart)`; null when the ID is unknown or outside the standard `BodyPart` range. */
	fun bodyPartOf(id: BoneId): BodyPart? {
		if (this[id] == null) return null
		return BODY_PART_BY_VALUE.getOrNull(id.value.toInt())?.takeIf { it != BodyPart.NONE }
	}

	/** Inverse of `get(String)`. */
	fun keyOf(id: BoneId): String? = this[id]?.key
	fun parentOf(id: BoneId): BoneId? = this[id]?.parent?.takeIf { it != 0.toUShort() }?.let(::BoneId)
	fun childrenOf(id: BoneId): List<BoneId> = (childIds.getOrNull(id.value.toInt()) ?: IntArray(0)).map { BoneId(it.toUShort()) }

	// Hierarchy is fixed for the registry's lifetime, so traversals are too: computed once here,
	// not memoized lazily on first use. This registry is shared across every consumer once
	// frozen, each free to call hierarchyFrom from its own dispatcher; a lazily-filled plain
	// array has no happens-before edge between an unsynchronized writer and a reader on another
	// thread, which a mutable ArrayList published that way can turn into a torn read instead of
	// just wasted recomputation. Built once, single-threaded, during construction instead.
	private val hierarchyCache: Array<List<Pair<BoneId?, BoneId>>> = Array(2 * (maxId + 1)) { cacheIndex ->
		val root = BoneId((cacheIndex / 2).toUShort())
		val onlyChildren = cacheIndex % 2 == 1
		val result = mutableListOf<Pair<BoneId?, BoneId>>()
		fun visit(parent: BoneId?, id: BoneId, skipSelf: Boolean) {
			if (!skipSelf) result += parent to id
			for (childId in childIds.getOrNull(id.value.toInt()) ?: IntArray(0)) {
				visit(id, BoneId(childId.toUShort()), false)
			}
		}
		visit(null, root, onlyChildren)
		result
	}

	fun hierarchyFrom(root: BoneId, onlyChildren: Boolean = false): List<Pair<BoneId?, BoneId>> = hierarchyCache[root.value.toInt() * 2 + if (onlyChildren) 1 else 0]

	/** Cached: a full rebuild starts from [root] alone, the most common call to [highest]. */
	val rootSet: BoneSet = BoneSet.of(this, listOf(root))

	/** Of [ids], the ones with no ancestor also in [ids]. */
	fun highest(ids: BoneSet): BoneSet {
		// root is everyone's ancestor, so if it changed it's the only "highest" bone.
		if (root in ids) return rootSet
		val result = BoneSet.of(this, ids)
		for (id in ids) {
			var parent = parentOf(id)
			while (parent != null) {
				if (parent in ids) {
					result.remove(id)
					break
				}
				parent = parentOf(parent)
			}
		}
		return result
	}

	companion object {
		fun standard(): BoneRegistry {
			val parts = BodyPart.entries.filter { it != BodyPart.NONE }
			val parentPartOf = buildMap {
				for ((parent, children) in BODY_PART_HIERARCHY_MAP) for (child in children) put(child, parent)
			}
			return from(
				WireBoneRegistry(
					bones = parts.map { bodyPart ->
						BoneDefinition(
							id = bodyPart.boneId.value,
							key = bodyPart.key,
							displayName = bodyPart.name.replace('_', ' ').lowercase(),
							parent = parentPartOf[bodyPart]?.boneId?.value ?: 0.toUShort(),
						)
					},
				),
			)
		}

		fun from(registry: WireBoneRegistry): BoneRegistry {
			val n = registry.bones.size
			val definitions = registry.bones.associateBy { it.id }
			require(definitions.size == n) { "Bone registry contains duplicate IDs" }
			require(registry.bones.map { it.key }.toSet().size == n) { "Bone registry contains duplicate keys" }
			require(definitions.keys == (1..n).map { it.toUShort() }.toSet()) {
				"Bone registry IDs must be exactly 1..$n"
			}
			require(registry.bones.all { it.parent == 0.toUShort() || it.parent in definitions }) { "Bone registry has an unknown parent" }
			val visited = mutableSetOf<UShort>()
			fun root(id: UShort): UShort {
				require(visited.add(id)) { "Bone registry contains a cycle" }
				val parent = definitions.getValue(id).parent
				return if (parent == 0.toUShort()) id else root(parent)
			}
			definitions.keys.forEach {
				visited.clear()
				root(it)
			}

			val byId = arrayOfNulls<BoneDefinition>(n + 1)
			val children = Array(n + 1) { mutableListOf<Int>() }
			var root: BoneId? = null
			for (definition in registry.bones) {
				val id = definition.id.toInt()
				byId[id] = definition
				if (definition.parent == 0.toUShort()) {
					require(root == null) { "Bone registry has more than one root: a skeleton has exactly one" }
					root = BoneId(definition.id)
				} else {
					children[definition.parent.toInt()] += id
				}
			}
			return BoneRegistry(
				value = registry,
				maxId = n,
				root = requireNotNull(root) { "Bone registry has no root" },
				byId = byId,
				byKey = registry.bones.associateBy { it.key },
				childIds = Array(children.size) { children[it].toIntArray() },
			)
		}
	}
}
