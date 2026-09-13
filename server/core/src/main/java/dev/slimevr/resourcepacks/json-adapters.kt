package dev.slimevr.resourcepacks

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

private fun Decoder.requireJson(): JsonDecoder = this as? JsonDecoder ?: throw SerializationException("Resource packs require a JSON decoder")
private fun Encoder.requireJson(): JsonEncoder = this as? JsonEncoder ?: throw SerializationException("Resource packs require a JSON encoder")
private fun JsonElement.objectValue(): JsonObject = this as? JsonObject ?: throw SerializationException("Expected a JSON object")

object VmcNamesSerializer : KSerializer<VmcNames> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VmcNames", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder) = decoder.requireJson().decodeJsonElement().let { VmcNames(if (it is JsonArray) it.map { name -> name.jsonPrimitive.content } else listOf(it.jsonPrimitive.content)) }
	override fun serialize(encoder: Encoder, value: VmcNames) = encoder.requireJson().encodeJsonElement(if (value.values.size == 1) JsonPrimitive(value.values.single()) else encoder.requireJson().json.encodeToJsonElement(value.values))
}

object VmcOutputSerializer : KSerializer<VmcOutput> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VmcOutput", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): VmcOutput {
		val json = decoder.requireJson().json
		val objectValue = decoder.requireJson().decodeJsonElement().objectValue()
		val parent = when (val raw = objectValue["inputParent"]) {
			null -> VmcInputParent.Omitted
			JsonNull -> VmcInputParent.ExplicitNull
			else -> VmcInputParent.Bone(raw.jsonPrimitive.content)
		}
		return VmcOutput(json.decodeFromJsonElement(objectValue["name"]!!), objectValue["outputParent"]?.jsonPrimitive?.contentOrNull, parent, objectValue["restRotation"]?.let { json.decodeFromJsonElement(it) })
	}
	override fun serialize(encoder: Encoder, value: VmcOutput) {
		val json = encoder.requireJson().json
		encoder.requireJson().encodeJsonElement(
			buildJsonObject {
				put("name", json.encodeToJsonElement(value.name))
				value.outputParent?.let { put("outputParent", it) }
				when (val parent = value.inputParent) {
					VmcInputParent.Omitted -> Unit
					VmcInputParent.ExplicitNull -> put("inputParent", JsonNull)
					is VmcInputParent.Bone -> put("inputParent", parent.key)
				}
				value.restRotation?.let { put("restRotation", json.encodeToJsonElement(it)) }
			},
		)
	}
}

object EulerSpecSerializer : KSerializer<EulerSpec> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EulerSpec", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): EulerSpec {
		val json = decoder.requireJson().json
		val value = decoder.requireJson().decodeJsonElement()
		if (value is JsonPrimitive) return EulerSpec(axis = json.decodeFromJsonElement(value))
		val objectValue = value.objectValue()
		return EulerSpec(objectValue["axis"]?.let { json.decodeFromJsonElement(it) }, objectValue["order"]?.let { json.decodeFromJsonElement(it) }, objectValue["unit"]?.let { json.decodeFromJsonElement(it) })
	}
	override fun serialize(encoder: Encoder, value: EulerSpec) {
		val json = encoder.requireJson().json
		if (value.order == null && value.unit == null && value.axis != null) {
			encoder.requireJson().encodeJsonElement(json.encodeToJsonElement(value.axis))
		} else {
			encoder.requireJson().encodeJsonElement(
				buildJsonObject {
					value.axis?.let { put("axis", json.encodeToJsonElement(it)) }
					value.order?.let { put("order", json.encodeToJsonElement(it)) }
					value.unit?.let { put("unit", json.encodeToJsonElement(it)) }
				},
			)
		}
	}
}

object ScalarOrVectorSerializer : KSerializer<ScalarOrVector> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ScalarOrVector", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): ScalarOrVector {
		val json = decoder.requireJson().json
		val value = decoder.requireJson().decodeJsonElement()
		return if (value is JsonPrimitive) ScalarOrVector.Scalar(value.float) else ScalarOrVector.Vector(json.decodeFromJsonElement(value))
	}
	override fun serialize(encoder: Encoder, value: ScalarOrVector) = encoder.requireJson().encodeJsonElement(
		when (value) {
			is ScalarOrVector.Scalar -> JsonPrimitive(value.value)
			is ScalarOrVector.Vector -> encoder.requireJson().json.encodeToJsonElement(value.value)
		},
	)
}

/** A language file has `$schema` alongside otherwise flat translation entries. */
object LanguageResourceSerializer : KSerializer<LanguageResource> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LanguageResource", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): LanguageResource {
		val document = decoder.requireJson().decodeJsonElement().objectValue()
		val schema = document["$" + "schema"]?.jsonPrimitive?.contentOrNull
		val translations = document.filterKeys { it != "$" + "schema" }.mapValues { (_, value) ->
			(value as? JsonPrimitive)?.content ?: throw SerializationException("Language values must be strings")
		}
		return LanguageResource(schema, translations)
	}
	override fun serialize(encoder: Encoder, value: LanguageResource) = encoder.requireJson().encodeJsonElement(
		buildJsonObject {
			value.schema?.let { put("$" + "schema", it) }
			value.translations.forEach { (key, translation) -> put(key, translation) }
		},
	)
}
