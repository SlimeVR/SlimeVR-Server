package dev.slimevr.desktop.udp

import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.toJavaAddress

// Prevent DNS lookup when getting the address
fun resolveDesktopUdpAddress(addr: InetSocketAddress): String = (addr.toJavaAddress() as java.net.InetSocketAddress).hostString
