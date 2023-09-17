package dev.slimevr.protocol.rpc.setup

import java.net.NetworkInterface

object RPCUtil {
	@JvmStatic
	fun getLocalIp(): String =
		NetworkInterface.getNetworkInterfaces().asSequence().first { netInt ->
			netInt.isUp && !netInt.isLoopback && !netInt.isVirtual && netInt.interfaceAddresses.any { it.broadcast != null }
		}.interfaceAddresses.filter { it.address.isSiteLocalAddress && it.broadcast != null }[0].address.hostAddress
}
