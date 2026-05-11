package dev.slimevr.desktop.vrchat

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

fun resolveDesktopOscQueryAddress(): String = try {
	val candidates = Collections.list(NetworkInterface.getNetworkInterfaces())
		.asSequence()
		.filter { iface -> iface.isUp && !iface.isLoopback && !iface.isVirtual }
		.flatMap { iface -> Collections.list(iface.inetAddresses).asSequence() }
		.filter { address -> !address.isLoopbackAddress && !address.hostAddress.contains(':') }
		.toList()

	val siteLocal = candidates.firstOrNull { address -> address.isSiteLocalAddress }
	(siteLocal ?: candidates.firstOrNull())?.hostAddress
		?: InetAddress.getLoopbackAddress().hostAddress
} catch (_: Exception) {
	InetAddress.getLoopbackAddress().hostAddress
}
