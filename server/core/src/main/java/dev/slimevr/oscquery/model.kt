package dev.slimevr.oscquery

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

val oscQueryJson = Json {
	encodeDefaults = true
	ignoreUnknownKeys = true
	// OSCQuery clients (notably VRChat) treat omitted fields as "absent" rather
	// than "null"; some refuse responses peppered with explicit null values.
	explicitNulls = false
}

enum class OscQueryTransport {
	TCP,
	UDP,
}

enum class OscQueryExtension {
	ACCESS,
	VALUE,
	RANGE,
	DESCRIPTION,
	TAGS,
	EXTENDED_TYPE,
	UNIT,
	CRITICAL,
	CLIPMODE,
	OVERLOADS,
	HTML,
	LISTEN,
}

@Serializable
data class OscQueryHostInfo(
	@SerialName("NAME")
	val name: String,
	@SerialName("OSC_IP")
	val oscIp: String,
	@SerialName("OSC_PORT")
	val oscPort: UShort,
	@SerialName("OSC_TRANSPORT")
	val oscTransport: OscQueryTransport = OscQueryTransport.UDP,
	@SerialName("WS_IP")
	val websocketIp: String? = null,
	@SerialName("WS_PORT")
	val websocketPort: UShort? = null,
	@SerialName("EXTENSIONS")
	val extensions: Map<OscQueryExtension, Boolean> = mapOf(OscQueryExtension.VALUE to true),
)

object OscQueryAccess {
	const val READ = 1
	const val WRITE = 2
	const val READ_WRITE = READ or WRITE
}

@Serializable
data class OscQueryNode(
	@SerialName("FULL_PATH")
	val fullPath: String,
	@SerialName("TYPE")
	val type: String? = null,
	@SerialName("ACCESS")
	val access: Int? = null,
	@SerialName("VALUE")
	val value: List<JsonElement>? = null,
	@SerialName("DESCRIPTION")
	val description: String? = null,
	@SerialName("CONTENTS")
	val contents: Map<String, OscQueryNode>? = null,
) {
	val name: String
		get() = fullPath.substringAfterLast('/').ifEmpty { "/" }

	val parentPath: String
		get() {
			if (fullPath == "/") return "/"
			val parentEnd = fullPath.lastIndexOf('/').coerceAtLeast(0)
			return fullPath.substring(0, parentEnd).ifEmpty { "/" }
		}
}

data class OscQueryService(
	val name: String,
	val addresses: List<String>,
	val host: String,
	val type: String,
	val port: Int,
	val txt: Map<String, String>,
)
