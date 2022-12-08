package dev.slimevr.platform.linux;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
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
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;


public class UnixSocketBridge extends SteamVRBridge implements AutoCloseable {
	public final String socketPath;
	public final UnixDomainSocketAddress socketAddress;
	private final ByteBuffer dst = ByteBuffer.allocate(2048);
	private final ByteBuffer src = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN);

	private ServerSocketChannel server;
	private SocketChannel channel;
	private boolean socketError = false;

	public UnixSocketBridge(
		VRServer server,
		HMDTracker hmd,
		String bridgeSettingsKey,
		String bridgeName,
		String socketPath,
		List<? extends ShareableTracker> shareableTrackers
	) {
		super(server, hmd, "Named socket thread", bridgeName, bridgeSettingsKey, shareableTrackers);
		this.socketPath = socketPath;
		this.socketAddress = UnixDomainSocketAddress.of(socketPath);
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
		LogManager.debug("Sending msg: " + message.toString());
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
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updateSocket() throws IOException {
		int read = channel.read(dst);
		boolean readAnything = false;
		// if buffer has 4 bytes at least, we got the message size!
		if (read > 0 && dst.remaining() >= 4) {
			int messageLength = dst.get(0) | dst.get(1) << 8 | dst.get(2) << 16 | dst.get(3) << 24;
			if (messageLength > 1024) { // Overflow
				LogManager
					.severe(
						"["
							+ bridgeName
							+ "] Buffer overflow on socket. Message length: "
							+ messageLength
					);
				socketError = true;
			} else if (dst.remaining() >= messageLength) {
				// Parse the message (this reads the array directly from the
				// dst, so we need to move position ourselves)
				try {
					var message = parseMessage(dst.array(), 4, messageLength - 4);
					LogManager.debug("Receiving msg " + message.toString());
					this.messageReceived(message);
				} catch (InvalidProtocolBufferException e) {
					LogManager.severe("Failed to read protocol message", e);
				}
				dst.position(messageLength);
				dst.compact();
				readAnything = true;
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

	private static ProtobufMessages.ProtobufMessage parseMessage(
		byte[] data,
		int offset,
		int length
	) throws InvalidProtocolBufferException {
		return ProtobufMessages.ProtobufMessage
			.parser()
			.parseFrom(data, offset, length);
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
		server.bind(this.socketAddress);
		LogManager.info("[" + bridgeName + "] Socket " + this.socketPath + " created");
		return server;
	}

	@Override
	public void close() throws Exception {
		if (this.server != null) {
			this.server.close();
		}
	}

	@Override
	public boolean isConnected() {
		return channel.isConnected();
	}
}

