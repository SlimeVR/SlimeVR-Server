package dev.slimevr.config.serializers

import io.github.axisangles.ktmath.ObjectQuaternion
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object QuaternionSerializer : KSerializer<ObjectQuaternion> {
	@Serializable
	private data class QuaternionSurrogate(val x: Float, val y: Float, val z: Float, val w: Float)

	override val descriptor: SerialDescriptor = QuaternionSurrogate.serializer().descriptor

	override fun serialize(encoder: Encoder, value: ObjectQuaternion) {
		val surrogate = QuaternionSurrogate(value.x, value.y, value.z, value.w)
		encoder.encodeSerializableValue(QuaternionSurrogate.serializer(), surrogate)
	}

	override fun deserialize(decoder: Decoder): ObjectQuaternion {
		val surrogate = decoder.decodeSerializableValue(QuaternionSurrogate.serializer())
		return ObjectQuaternion(surrogate.x, surrogate.y, surrogate.z, surrogate.w)
	}
}
