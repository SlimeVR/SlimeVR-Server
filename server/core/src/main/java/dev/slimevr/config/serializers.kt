package dev.slimevr.config

import io.github.axisangles.ktmath.Quaternion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.KeybindId

object KeybindIdSerializer : KSerializer<KeybindId> {
	override val descriptor = PrimitiveSerialDescriptor("KeybindId", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: KeybindId) {
		(encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value.name))
	}

	override fun deserialize(decoder: Decoder): KeybindId {
		val element = (decoder as JsonDecoder).decodeJsonElement()
		// Falling back to a real keybind would silently turn an unknown entry into a duplicate
		return KeybindId.entries.firstOrNull { it.name == element.jsonPrimitive.content }
			?: KeybindId.NONE
	}
}

object BodyPartSerializer : KSerializer<BodyPart?> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = PrimitiveSerialDescriptor("BodyPart", PrimitiveKind.STRING).nullable

	override fun serialize(encoder: Encoder, value: BodyPart?) {
		val jsonEncoder = encoder as JsonEncoder
		if (value == null) {
			jsonEncoder.encodeJsonElement(JsonNull)
		} else {
			jsonEncoder.encodeJsonElement(JsonPrimitive(value.name))
		}
	}

	override fun deserialize(decoder: Decoder): BodyPart? {
		val element = (decoder as JsonDecoder).decodeJsonElement()
		if (element is JsonNull) return null
		return BodyPart.entries.firstOrNull { it.name == element.jsonPrimitive.content }
	}
}

object BodyPartListSerializer : KSerializer<List<BodyPart?>> by ListSerializer(BodyPartSerializer)

@Serializable
private data class QuaternionSurrogate(val w: Float, val x: Float, val y: Float, val z: Float)

object QuaternionSerializer : KSerializer<Quaternion> {
	override val descriptor = QuaternionSurrogate.serializer().descriptor
	override fun serialize(encoder: Encoder, value: Quaternion) = encoder.encodeSerializableValue(QuaternionSurrogate.serializer(), QuaternionSurrogate(value.w, value.x, value.y, value.z))
	override fun deserialize(decoder: Decoder): Quaternion {
		val s = decoder.decodeSerializableValue(QuaternionSurrogate.serializer())
		return Quaternion(s.w, s.x, s.y, s.z)
	}
}
