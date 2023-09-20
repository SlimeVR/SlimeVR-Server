package dev.slimevr.protocol.rpc.setup

import java.net.NetworkInterface

object RPCUtil {
	@JvmStatic
	fun getLocalIp(): String =
		NetworkInterface.getNetworkInterfaces().asSequence().first { netInt ->
			netInt.isUp && !netInt.isLoopback && !netInt.isVirtual && netInt.interfaceAddresses.any { it.address.isSiteLocalAddress && it.broadcast != null }
		}.interfaceAddresses.first {
			it.address.isSiteLocalAddress && it.broadcast != null
		}.address.hostAddress
}
