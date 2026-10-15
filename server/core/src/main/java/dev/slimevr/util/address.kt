package dev.slimevr.util

fun stripIpAddressPort(string: String) = string.lastIndexOf('/').takeIf { it != -1 }?.let {
	string.substring(0, it)
} ?: string

fun ipv4AddressFromString(address: String): UInt {
	val parts = stripIpAddressPort(address).split('.')
	if (parts.size != 4) return 0u
	return parts.fold(0u) { acc, part ->
		val value = part.toUIntOrNull()?.takeIf { it <= 255u } ?: return 0u
		(acc shl 8) or value
	}
}
