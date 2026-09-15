package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.languages.LanguageResource
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
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonPrimitive

private fun Decoder.requireJson(): JsonDecoder = this as? JsonDecoder ?: throw SerializationException("Resource packs require a JSON decoder")
private fun JsonElement.objectValue(): JsonObject = this as? JsonObject ?: throw SerializationException("Expected a JSON object")

private fun neverEncoded(): Nothing = error("Resource packs are never re-encoded")

object VmcNamesSerializer : KSerializer<VmcNames> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("VmcNames", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder) = decoder.requireJson().decodeJsonElement().let { VmcNames(if (it is JsonArray) it.map { name -> name.jsonPrimitive.content } else listOf(it.jsonPrimitive.content)) }
	override fun serialize(encoder: Encoder, value: VmcNames) = neverEncoded()
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
	override fun serialize(encoder: Encoder, value: VmcOutput) = neverEncoded()
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
	override fun serialize(encoder: Encoder, value: EulerSpec) = neverEncoded()
}

object ScalarOrVectorSerializer : KSerializer<ScalarOrVector> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ScalarOrVector", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): ScalarOrVector {
		val json = decoder.requireJson().json
		val value = decoder.requireJson().decodeJsonElement()
		return if (value is JsonPrimitive) ScalarOrVector.Scalar(value.float) else ScalarOrVector.Vector(json.decodeFromJsonElement(value))
	}
	override fun serialize(encoder: Encoder, value: ScalarOrVector) = neverEncoded()
}

object PipelineStepSerializer : KSerializer<PipelineStep> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PipelineStep", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): PipelineStep {
		val json = decoder.requireJson().json
		val (key, value) = decoder.requireJson().decodeJsonElement().objectValue().entries.singleOrNull()
			?: throw SerializationException("A pipeline step must have exactly one operation")
		return when (key) {
			"euler" -> PipelineStep.Euler(json.decodeFromJsonElement(value))
			"scale" -> PipelineStep.Scale(json.decodeFromJsonElement(value))
			"divide" -> PipelineStep.Divide(json.decodeFromJsonElement(value))
			"offset" -> PipelineStep.Offset(json.decodeFromJsonElement(value))
			"clamp" -> PipelineStep.Clamp(json.decodeFromJsonElement(value))
			"greaterThan" -> PipelineStep.GreaterThan(json.decodeFromJsonElement(value))
			"lessThan" -> PipelineStep.LessThan(json.decodeFromJsonElement(value))
			else -> throw SerializationException("Unknown pipeline step operation '$key'")
		}
	}
	override fun serialize(encoder: Encoder, value: PipelineStep) = neverEncoded()
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
	override fun serialize(encoder: Encoder, value: LanguageResource) = neverEncoded()
}
