package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart
import java.util.EnumMap

typealias BodyPartMap<V> = EnumMap<BodyPart, V>

@PublishedApi
internal val ALL_BODY_PARTS: Array<BodyPart> = BodyPart.entries.toTypedArray()

fun <V> bodyPartMap(): BodyPartMap<V> = EnumMap(BodyPart::class.java)

inline fun <V> BodyPartMap<V>.mutate(block: (BodyPartMap<V>) -> Unit): BodyPartMap<V> = EnumMap(this).also(block)

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
