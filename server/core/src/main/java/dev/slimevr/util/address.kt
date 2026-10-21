package dev.slimevr.util

fun stripIpAddressPort(string: String) = string.lastIndexOf('/').takeIf { it != -1 }?.let {
	string.substring(0, it)
} ?: string

fun udpHardwareId(macAddress: String?, address: String): String = macAddress ?: stripIpAddressPort(address)

fun ipv4AddressFromString(address: String): UInt {
	val parts = stripIpAddressPort(address).split('.')
	if (parts.size != 4) return 0u
	return parts.fold(0u) { acc, part ->
		val value = part.toUIntOrNull()?.takeIf { it <= 255u } ?: return 0u
		(acc shl 8) or value
	}
}

fun macAddressToULong(macAddress: String): ULong? {
	val bytes = macAddress.split(':')
	if (bytes.size != 6) return null
	return bytes.fold(0uL) { acc, byte ->
		val value = byte.toULongOrNull(16)?.takeIf { byte.length == 2 } ?: return null
		(acc shl 8) or value
	}
}
