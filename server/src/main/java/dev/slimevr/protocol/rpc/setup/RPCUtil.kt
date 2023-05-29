package dev.slimevr.protocol.rpc.setup

import java.net.NetworkInterface

object RPCUtil {
	@JvmStatic
	fun getLocalIp(): String =
		NetworkInterface.getNetworkInterfaces().asSequence().first {
			it.isUp && !it.isLoopback && !it.isVirtual
		}.interfaceAddresses.first {
			it.broadcast != null
		}.address.hostAddress
}
