package dev.slimevr.util

fun stripIpAddressPort(string: String) = string.lastIndexOf('/').takeIf { it != -1 }?.let {
	string.substring(0, it)
} ?: string
