package dev.slimevr.desktop.platform.linux

import java.io.IOException
import java.net.StandardProtocolFamily
import java.net.UnixDomainSocketAddress
import java.nio.channels.SocketChannel

object SocketUtils {
	fun isSocketInUse(socketPath: String) = try {
		SocketChannel.open(StandardProtocolFamily.UNIX).use { testChannel ->
			testChannel.connect(UnixDomainSocketAddress.of(socketPath))
			true
		}
	} catch (_: IOException) {
		false
	}
}
