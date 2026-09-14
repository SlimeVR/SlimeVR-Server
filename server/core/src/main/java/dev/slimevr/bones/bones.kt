package dev.slimevr.bones

import java.util.EnumMap

/** A registry-local bone handle. `0` is reserved and never resolves to a definition. */
@JvmInline
value class BoneId(val value: UShort) {
	override fun toString(): String = "BoneId($value)"
}

/**
 * The [BoneId] a standard bone always has. [BoneRegistry.standard] assigns every standard
 * bone's ID to its own [BodyPart.value], so this needs no registry lookup. A dynamic [BoneId]
 * (e.g. from a tracker assignment) may be an extension bone, so going the other way needs
 * [BoneRegistry.bodyPartOf] instead.
 */
val BodyPart.boneId: BoneId get() = BoneId(value.toUShort())

/**
 * The registry key a standard bone always has: `"slimevr:" + name.lowercase()`. Keys are
 * namespaced (`namespace:path`) so extension bones can't collide with standard ones or each
 * other. Use this for compile-time constants (e.g. config defaults); elsewhere resolve a key
 * through an actual [BoneRegistry], since an arbitrary stored key might not be standard.
 */
val BodyPart.key: String get() = "slimevr:${name.lowercase()}"

/**
 * Static anatomy tables (constraints, IK chains, external-format mappings) are keyed by
 * [BodyPart] below since every key is a standard body part. [BoneId]-keyed state (inputs,
 * computed poses, IK targets, tracker assignment) uses [BoneMap]/[BoneSet] instead, since a
 * bone the registry defines might not have a standard body part at all.
 */
typealias BodyPartMap<V> = EnumMap<BodyPart, V>

@PublishedApi
internal val ALL_BODY_PARTS: Array<BodyPart> = BodyPart.entries.toTypedArray()

fun <V> bodyPartMap(): BodyPartMap<V> = EnumMap(BodyPart::class.java)

inline fun <V> BodyPartMap<V>.mutateCopy(block: (BodyPartMap<V>) -> Unit): BodyPartMap<V> = EnumMap(this).also(block)

inline fun <V : Any, R : Any> BodyPartMap<V>.mapValues(transform: (BodyPart, V) -> R): BodyPartMap<R> {
	val result = bodyPartMap<R>()
	for (bodyPart in ALL_BODY_PARTS) {
		result[bodyPart] = transform(bodyPart, this[bodyPart] ?: continue)
	}
	return result
}

inline fun <V : Any> BodyPartMap<V>.forEachBone(action: (BodyPart, V) -> Unit) {
	for (bodyPart in ALL_BODY_PARTS) {
		action(bodyPart, this[bodyPart] ?: continue)
	}
}

/** Converts a [BodyPartMap] to a plain [BoneId]-keyed [Map]; no registry needed since every key is standard. */
fun <V : Any> BodyPartMap<V>.resolveToBoneIds(): Map<BoneId, V> = mapKeys { it.key.boneId }

/**
 * Dense array-backed map keyed by [BoneId], scoped to the [registry] it was built for.
 * IDs run `1..registry.maxId`, slot 0 is unused.
 *
 * Extends the read-only [AbstractMap]. Mutation only goes through [set]/[copy]/[mutateCopy],
 * never through the `Map` interface, so there's no mutable-iterator/`Entry` machinery to write.
 */
class BoneMap<V> private constructor(
	val registry: BoneRegistry,
	private val array: Array<Any?>,
) : AbstractMap<BoneId, V>() {

	private fun indexOf(key: BoneId): Int {
		val index = key.value.toInt()
		require(index in 1 until array.size) { "$key is outside this registry" }
		return index
	}

	@Suppress("UNCHECKED_CAST")
	override fun get(key: BoneId): V? {
		val index = key.value.toInt()
		if (index == 0) return null
		require(index in 1 until array.size) { "$key is outside this registry" }
		return array[index] as V?
	}

	operator fun set(key: BoneId, value: V) {
		array[indexOf(key)] = value
	}

	override val entries: Set<Map.Entry<BoneId, V>>
		get() = buildSet {
			for (index in 1 until array.size) {
				@Suppress("UNCHECKED_CAST")
				val value = array[index] as V? ?: continue
				add(java.util.AbstractMap.SimpleImmutableEntry(BoneId(index.toUShort()), value))
			}
		}

	/** Copies into a fresh array; the original is untouched by later mutation. */
	fun copy(): BoneMap<V> = BoneMap(registry, array.copyOf())

	companion object {
		fun <V> of(registry: BoneRegistry): BoneMap<V> = BoneMap(registry, arrayOfNulls(registry.maxId + 1))
	}
}

/** Mutates a [copy][BoneMap.copy] of this map, leaving the receiver untouched. */
inline fun <V> BoneMap<V>.mutateCopy(block: (BoneMap<V>) -> Unit): BoneMap<V> = copy().also(block)

/** Like the stdlib `mapValues`, but keeps the result a [BoneMap] scoped to the same registry. */
inline fun <V, R : Any> BoneMap<V>.mapValues(transform: (BoneId, V) -> R): BoneMap<R> {
	val result = BoneMap.of<R>(registry)
	for ((id, value) in this) result[id] = transform(id, value)
	return result
}

/**
 * Dense boolean-array-backed set of [BoneId], scoped to a [registry]. Avoids boxing a `UShort`
 * per element, which a generic `MutableSet<BoneId>` would do.
 *
 * Extends the read-only [AbstractSet]. Mutation only goes through [add]/[remove]/[clear] as
 * plain methods, so there's no `MutableIterator` to write.
 */
class BoneSet private constructor(
	val registry: BoneRegistry,
	private val present: BooleanArray,
) : AbstractSet<BoneId>() {
	private var count = 0

	override val size: Int get() = count

	override fun contains(element: BoneId): Boolean = present.getOrNull(element.value.toInt()) == true

	fun add(element: BoneId): Boolean {
		val index = element.value.toInt()
		require(index in 1 until present.size) { "$element is outside this registry" }
		if (present[index]) return false
		present[index] = true
		count++
		return true
	}

	fun addAll(elements: Iterable<BoneId>): Boolean {
		var changed = false
		for (element in elements) if (add(element)) changed = true
		return changed
	}

	fun remove(element: BoneId): Boolean {
		val index = element.value.toInt()
		if (index !in present.indices || !present[index]) return false
		present[index] = false
		count--
		return true
	}

	fun clear() {
		present.fill(false)
		count = 0
	}

	override fun iterator(): Iterator<BoneId> = present.indices.asSequence()
		.filter { it != 0 && present[it] }
		.map { BoneId(it.toUShort()) }
		.iterator()

	companion object {
		fun of(registry: BoneRegistry): BoneSet = BoneSet(registry, BooleanArray(registry.maxId + 1))
		fun of(registry: BoneRegistry, ids: Iterable<BoneId>): BoneSet = of(registry).also { it.addAll(ids) }
	}
}
