package dev.slimevr.resourcepacks

import kotlinx.serialization.json.JsonObject

fun deepMerge(target: JsonObject, source: JsonObject): JsonObject {
	val result = target.toMutableMap()
	for ((k, v) in source) {
		if (k == "\$extend") continue
		val existing = result[k]
		if (existing is JsonObject && v is JsonObject) {
			result[k] = deepMerge(existing, v)
		} else {
			result[k] = v
		}
	}
	return JsonObject(result)
}
