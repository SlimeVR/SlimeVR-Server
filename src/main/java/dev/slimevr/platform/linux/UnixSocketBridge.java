package dev.slimevr.platform.linux;

import com.google.protobuf.CodedOutputStream;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;


public class UnixSocketBridge extends SteamVRBridge implements AutoCloseable {
	public static final String SOCKET_PATH = "/tmp/SlimeVRDriver";
	public static final UnixDomainSocketAddress SOCKET_ADDRESS = UnixDomainSocketAddress
		.of(SOCKET_PATH);
	private final ByteBuffer dst = ByteBuffer.allocate(2048);
	private final ByteBuffer src = ByteBuffer.allocate(2048);

	private ServerSocketChannel server;
	private SocketChannel channel;
	private boolean socketError = false;

	public UnixSocketBridge(
		VRServer server,
		HMDTracker hmd,
		String bridgeSettingsKey,
		String bridgeName,
		List<? extends ShareableTracker> shareableTrackers
	) {
		super(server, hmd, "Named socket thread", bridgeName, bridgeSettingsKey, shareableTrackers);
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			this.server = createSocket();
			while (true) {
				if (this.channel == null) {
					this.channel = server.accept();
					if (this.channel == null)
						continue;
					Main.vrServer.queueTask(this::reconnected);
					LogManager
						.info(
							"["
								+ bridgeName
								+ "]"
								+ " Connected to "
								+ this.channel.getRemoteAddress().toString()
						);
				} else {
					if (this.socketError || !this.channel.isConnected()) {
						this.resetChannel();
						continue;
					}
					boolean update = this.updateSocket();
					if (!update) {
						try {
							Thread.sleep(5); // Up to 200Hz
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@BridgeThread
	protected boolean sendMessageReal(ProtobufMessages.ProtobufMessage message) {
		if (this.channel != null) {
			try {
				int size = message.getSerializedSize() + 4;
				this.src.putInt(size);
				CodedOutputStream os = CodedOutputStream.newInstance(this.src);
				message.writeTo(os);
				this.src.flip();

				while (this.src.hasRemaining()) {
					channel.write(this.src);
				}

				this.src.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updateSocket() throws IOException {
		int read = channel.read(dst);
		boolean readAnything = false;
		if (read > 0) {
			if (read >= 4) { // Got size
				dst.mark();
				int messageLength = dst.getInt();
				dst.reset();
				if (messageLength > 1024) { // Overflow
					LogManager
						.severe(
							"["
								+ bridgeName
								+ "] Buffer overflow on socket. Message length: "
								+ messageLength
						);
					socketError = true;
					return readAnything;
				}
				if (read >= messageLength) {
					ProtobufMessages.ProtobufMessage message = ProtobufMessages.ProtobufMessage
						.parser()
						.parseFrom(dst.array(), 4 + dst.position(), messageLength - 4);
					this.messageReceived(message);
					readAnything = true;
					dst.reset();
				}
			}
		} else if (read == -1) {
			LogManager
				.info(
					"["
						+ bridgeName
						+ "] Reached end-of-stream on connection of "
						+ this.channel.getRemoteAddress().toString()
				);
			socketError = true;
		}
		return readAnything;
	}

	private void resetChannel() throws IOException {
		LogManager
			.info(
				"["
					+ bridgeName
					+ "] Disconnected from "
					+ this.channel.getRemoteAddress().toString()
			);
		this.channel.close();
		this.channel = null;
		this.socketError = false;
		this.dst.clear();
		Main.vrServer.queueTask(this::disconnected);
	}

	private ServerSocketChannel createSocket() throws IOException {
		ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
		server.bind(SOCKET_ADDRESS);
		LogManager.info("[" + bridgeName + "] Socket " + SOCKET_PATH + " created");
		return server;
	}

	@Override
	public void close() throws Exception {
		if (this.server != null) {
			this.server.close();
		}
	}
}

