package dev.slimevr.resourcepacks

import kotlinx.serialization.json.*

fun applyJsonPatch(target: JsonObject, patch: JsonArray): JsonObject {
    var current = target
    for (op in patch) {
        val obj = op.jsonObject
        val path = obj["path"]?.jsonPrimitive?.content ?: continue
        val keys = path.trim('/').split('/')
        when (obj["op"]?.jsonPrimitive?.content) {
            "replace" -> current = setPath(current, keys, obj["value"]!!)
            "add" -> current = setPath(current, keys, obj["value"]!!)
            "remove" -> current = removePath(current, keys)
        }
    }
    return current
}

private fun setPath(target: JsonObject, keys: List<String>, value: JsonElement): JsonObject {
    if (keys.isEmpty()) return target
    val key = keys.first()
    if (keys.size == 1) {
        val mut = target.toMutableMap()
        mut[key] = value
        return JsonObject(mut)
    }
    val child = target[key]?.jsonObject ?: JsonObject(emptyMap())
    val mut = target.toMutableMap()
    mut[key] = setPath(child, keys.drop(1), value)
    return JsonObject(mut)
}

private fun removePath(target: JsonObject, keys: List<String>): JsonObject {
    if (keys.isEmpty()) return target
    val key = keys.first()
    if (keys.size == 1) {
        val mut = target.toMutableMap()
        mut.remove(key)
        return JsonObject(mut)
    }
    val child = target[key]?.jsonObject ?: return target
    val mut = target.toMutableMap()
    mut[key] = removePath(child, keys.drop(1))
    return JsonObject(mut)
}
