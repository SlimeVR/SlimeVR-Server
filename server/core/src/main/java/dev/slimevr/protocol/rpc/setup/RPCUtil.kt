package dev.slimevr.protocol.rpc.setup

import java.net.NetworkInterface

object RPCUtil {
	@JvmStatic
	fun getLocalIp(): String? {
		for (netInt in NetworkInterface.getNetworkInterfaces()) {
			if (netInt.isUp && !netInt.isLoopback && !netInt.isVirtual) {
				for (netAddr in netInt.interfaceAddresses) {
					if (netAddr.address.isSiteLocalAddress && netAddr.broadcast != null) {
						return netAddr.address.hostAddress
					}
				}
			}
		}
		return null
	}
}
