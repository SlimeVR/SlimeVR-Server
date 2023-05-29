package dev.slimevr.platform.linux;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.tracking.trackers.Tracker;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;

import java.io.File;
import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.util.List;


public class UnixSocketBridge extends SteamVRBridge implements AutoCloseable {
	public final String socketPath;
	public final UnixDomainSocketAddress socketAddress;
	private final ByteBuffer dst = ByteBuffer.allocate(2048);
	private final ByteBuffer src = ByteBuffer.allocate(2048).order(ByteOrder.LITTLE_ENDIAN);

	private ServerSocketChannel server;
	private SocketChannel channel;
	private Selector selector;
	private boolean socketError = false;

	public UnixSocketBridge(
		VRServer server,
		Tracker hmd,
		String bridgeSettingsKey,
		String bridgeName,
		String socketPath,
		List<Tracker> shareableTrackers
	) {
		super(server, hmd, "Named socket thread", bridgeName, bridgeSettingsKey, shareableTrackers);
		this.socketPath = socketPath;
		this.socketAddress = UnixDomainSocketAddress.of(socketPath);

		File socketFile = new File(socketPath);
		if (socketFile.exists()) {
			throw new RuntimeException(socketPath + " socket already exists.");
		}
		socketFile.deleteOnExit();
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			this.server = createSocket();
			while (true) {
				if (this.channel == null) {
					reportDisconnected();
					this.selector = Selector.open();
					this.channel = server.accept();
					this.channel.configureBlocking(false);
					this.channel.register(this.selector, SelectionKey.OP_READ);
					if (this.channel == null)
						continue;
					Main.getVrServer().queueTask(this::reconnected);
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
					try {
						boolean updated = this.updateSocket();
						updateMessageQueue();
						if (!updated) {
							this.waitForData(10);
						} else if (lastSteamVRStatus != 0) {
							Main.getVrServer().getStatusSystem().removeStatusInt(lastSteamVRStatus);
							lastSteamVRStatus = 0;
						}
					} catch (IOException ioError) {
						this.resetChannel();
						ioError.printStackTrace();
						try {
							Thread.sleep(10);
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
	@ThreadSafe
	protected void signalSend() {
		Selector selector = this.selector;
		if (selector == null) {
			return;
		}
		selector.wakeup();
	}

	@BridgeThread
	private void waitForData(long timeoutMs) throws IOException {
		this.selector.select(timeoutMs);
	}

	@Override
	@BridgeThread
	protected boolean sendMessageReal(ProtobufMessages.ProtobufMessage message) {
		if (this.channel != null) {
			try {
				int size = message.getSerializedSize() + 4;
				this.src.putInt(size);
				byte[] serialized = message.toByteArray();
				this.src.put(serialized);
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
		if (read == -1) {
			LogManager
				.info(
					"["
						+ bridgeName
						+ "] Reached end-of-stream on connection of "
						+ this.channel.getRemoteAddress().toString()
				);
			socketError = true;
			return false;
		} else if (read == 0) {
			return false;
		}

		boolean readAnything = false;
		// if buffer has 4 bytes at least, we got the message size!
		// processs all messages
		while (dst.position() >= 4) {
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
				break;
			} else if (dst.position() >= messageLength) {
				// Parse the message (this reads the array directly from the
				// dst, so we need to move position ourselves)
				try {
					var message = parseMessage(dst.array(), 4, messageLength - 4);
					this.messageReceived(message);
				} catch (InvalidProtocolBufferException e) {
					LogManager.severe("Failed to read protocol message", e);
				}
				int originalpos = dst.position();
				dst.position(messageLength);
				dst.compact();
				// move position after compacting
				dst.position(originalpos - messageLength);
				readAnything = true;
			} else {
				break;
			}
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
		this.selector.close();
		this.selector = null;
		this.channel.close();
		this.channel = null;
		this.socketError = false;
		this.dst.clear();
		Main.getVrServer().queueTask(this::disconnected);
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
		return channel != null && channel.isConnected();
	}
}

