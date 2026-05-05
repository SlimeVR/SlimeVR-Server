package dev.slimevr.osc

sealed class OscArg {
	data class Int(val value: kotlin.Int) : OscArg()
	data class Long(val value: kotlin.Long) : OscArg()
	data class Float(val value: kotlin.Float) : OscArg()
	data class Double(val value: kotlin.Double) : OscArg()
	data class String(val value: kotlin.String) : OscArg()
	data class Blob(val value: ByteArray) : OscArg() {
		override fun equals(other: Any?) = this === other || (other is Blob && value.contentEquals(other.value))
		override fun hashCode() = value.contentHashCode()
	}

	data object Impulse : OscArg()
	data object Null : OscArg()
	data object True : OscArg()
	data object False : OscArg()

	val typeTag: Char
		get() = when (this) {
			is Int -> 'i'
			is Long -> 'h'
			is Float -> 'f'
			is Double -> 'd'
			is String -> 's'
			is Blob -> 'b'
			Impulse -> 'I'
			Null -> 'N'
			True -> 'T'
			False -> 'F'
		}
}

data class OscMessage(val address: String, val args: List<OscArg> = emptyList())

data class OscBundle(val timetag: Long, val contents: List<OscContent> = emptyList())

sealed interface OscContent {
	data class Message(val msg: OscMessage) : OscContent
	data class Bundle(val bundle: OscBundle) : OscContent
}
