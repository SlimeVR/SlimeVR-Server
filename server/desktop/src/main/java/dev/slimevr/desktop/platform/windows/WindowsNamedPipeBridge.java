package dev.slimevr.desktop.platform.windows;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.desktop.platform.ProtobufMessages.ProtobufMessage;
import dev.slimevr.desktop.platform.SteamVRBridge;
import dev.slimevr.tracking.trackers.Tracker;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;


public class WindowsNamedPipeBridge extends SteamVRBridge
	implements WindowsNamedPipe.PipeMessageReader {
	protected final String pipeName;
	protected final String bridgeSettingsKey;
	protected WindowsNamedPipe pipe = null;
	protected WindowsNamedPipe.PipeConnection connection = null;
	private final byte[] buf = new byte[2048];

	public WindowsNamedPipeBridge(
		VRServer server,
		String bridgeSettingsKey,
		String bridgeName,
		String pipeName,
		List<Tracker> shareableTrackers
	) {
		super(
			server,
			bridgeName + " named pipe thread",
			bridgeName,
			bridgeSettingsKey,
			shareableTrackers
		);
		this.pipeName = pipeName;
		this.bridgeSettingsKey = bridgeSettingsKey;
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			pipe = new WindowsNamedPipe(this.pipeName, 1);
			while (true) {
				boolean pipesUpdated = false;
				if (connection == null) {
					// Report that our pipe is disconnected right now
					reportDisconnected();
					if ((connection = pipe.tryAccept()) != null)
						VRServer.Companion.getInstance().queueTask(this::reconnected);
				}
				if (connection != null) {
					if (connection.getState() == PipeState.OPEN) {
						pipesUpdated = connection.update(this);
						if (pipesUpdated) {
							reportConnected();
						}
						updateMessageQueue();
					}
					if (connection.getState() == PipeState.ERROR) {
						connection.disconnect();
						connection = null;
						disconnected();
					}
				}
				if (!pipesUpdated) {
					if (connection != null && connection.getState() == PipeState.OPEN) {
						connection.waitForData(10);
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// Do nothing.
						}
					}
				}
			}
		} catch (IOException e) {
			LogManager.severe("[" + bridgeName + "] Exception in runner thread", e);
		}
		pipe.close();
	}

	@Override
	@ThreadSafe
	protected void signalSend() {
		if (connection != null)
			connection.wakeUp();
	}

	@Override
	public void onMessage(WindowsNamedPipe.PipeConnection connection, ByteBuffer buf) {
		if (this.connection != connection) {
			LogManager
				.severe("[" + bridgeName + "] Got message from connection we don't know about");
			return;
		}

		try {
			ProtobufMessage message = ProtobufMessage
				.parser()
				.parseFrom(buf);
			messageReceived(message);
		} catch (InvalidProtocolBufferException parseEx) {
			LogManager.severe("[" + bridgeName + "] Failed to parse message", parseEx);
			connection.setError("Failed to parse message");
		}
	}

	@Override
	@BridgeThread
	protected boolean sendMessageReal(ProtobufMessage message) {
		if (connection == null || connection.getState() != PipeState.OPEN) {
			return false;
		}
		try {
			int size = message.getSerializedSize();
			CodedOutputStream os = CodedOutputStream.newInstance(buf, 4, size);
			message.writeTo(os);
			size += 4;

			buf[0] = (byte) (size & 0xFF);
			buf[1] = (byte) ((size >> 8) & 0xFF);
			buf[2] = (byte) ((size >> 16) & 0xFF);
			buf[3] = (byte) ((size >> 24) & 0xFF);

			return connection.sendBuffer(buf, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return connection != null && connection.getState() == PipeState.OPEN;
	}

	@NotNull
	@Override
	public String getBridgeConfigKey() {
		return this.bridgeSettingsKey;
	}
}
