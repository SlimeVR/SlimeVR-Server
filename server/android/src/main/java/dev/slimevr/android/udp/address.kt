package dev.slimevr.android.udp

import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.toJavaAddress

fun resolveAndroidUdpAddress(addr: InetSocketAddress): String = (addr.toJavaAddress() as java.net.InetSocketAddress).hostString
