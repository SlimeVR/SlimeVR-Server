package dev.slimevr.desktop.platform.linux;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.SocketChannel;


public class SocketUtils {

	static boolean isSocketInUse(String socketPath) {
		try (SocketChannel testChannel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
			testChannel.connect(UnixDomainSocketAddress.of(socketPath));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
