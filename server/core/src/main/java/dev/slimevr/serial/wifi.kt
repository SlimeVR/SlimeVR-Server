package dev.slimevr.serial

import java.nio.charset.StandardCharsets
import java.util.Base64

fun buildSetWifiCommand(ssid: String, password: String?): String {
	val encodedSsid = encodeBase64Utf8(ssid)
	val encodedPassword = password
		?.let(::encodeBase64Utf8)
		.orEmpty()
		.ifEmpty { "\"\"" }

	return "SET BWIFI $encodedSsid $encodedPassword\n"
}

private fun encodeBase64Utf8(value: String): String = Base64.getEncoder()
	.encodeToString(value.toByteArray(StandardCharsets.UTF_8))
