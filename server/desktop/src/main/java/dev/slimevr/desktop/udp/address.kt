package dev.slimevr.desktop.udp

import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.toJavaAddress

// Prevent DNS lookup when getting the address
fun resolveDesktopUdpAddress(addr: InetSocketAddress): String {
	val socketAddress = addr.toJavaAddress() as java.net.InetSocketAddress
	return "${socketAddress.hostString}/${socketAddress.port}"
}
