package dev.slimevr.platform.windows;

import com.google.protobuf.CodedOutputStream;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.tracking.trackers.*;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.util.List;


public class WindowsNamedPipeBridge extends SteamVRBridge {

	protected final String pipeName;
	private final byte[] buffArray = new byte[2048];
	protected WindowsPipe pipe;

	public WindowsNamedPipeBridge(
		VRServer server,
		HMDTracker hmd,
		String bridgeSettingsKey,
		String bridgeName,
		String pipeName,
		List<? extends ShareableTracker> shareableTrackers
	) {
		super(server, hmd, "Named pipe thread", bridgeName, bridgeSettingsKey, shareableTrackers);
		this.pipeName = pipeName;
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			createPipe();
			while (true) {
				boolean pipesUpdated = false;
				if (pipe.state == PipeState.CREATED) {
					tryOpeningPipe(pipe);
				}
				if (pipe.state == PipeState.OPEN) {
					pipesUpdated = updatePipe();
					updateMessageQueue();
				}
				if (pipe.state == PipeState.ERROR) {
					resetPipe();
				}
				if (!pipesUpdated) {
					try {
						Thread.sleep(5); // Up to 200Hz
					} catch (InterruptedException e) {
						e.printStackTrace();
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
	protected boolean sendMessageReal(ProtobufMessage message) {
		if (pipe.state == PipeState.OPEN) {
			try {
				int size = message.getSerializedSize();
				CodedOutputStream os = CodedOutputStream.newInstance(buffArray, 4, size);
				message.writeTo(os);
				size += 4;
				buffArray[0] = (byte) (size & 0xFF);
				buffArray[1] = (byte) ((size >> 8) & 0xFF);
				buffArray[2] = (byte) ((size >> 16) & 0xFF);
				buffArray[3] = (byte) ((size >> 24) & 0xFF);
				if (Kernel32.INSTANCE.WriteFile(pipe.pipeHandle, buffArray, size, null, null)) {
					return true;
				}
				pipe.state = PipeState.ERROR;
				LogManager
					.severe("[" + bridgeName + "] Pipe error: " + Kernel32.INSTANCE.GetLastError());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updatePipe() throws IOException {
		if (pipe.state == PipeState.OPEN) {
			boolean readAnything = false;
			IntByReference bytesAvailable = new IntByReference(0);
			while (
				Kernel32.INSTANCE
					.PeekNamedPipe(pipe.pipeHandle, buffArray, 4, null, bytesAvailable, null)
			) {
				if (bytesAvailable.getValue() >= 4) { // Got size
					int messageLength = (buffArray[3] << 24)
						| (buffArray[2] << 16)
						| (buffArray[1] << 8)
						| buffArray[0];
					if (messageLength > 1024) { // Overflow
						LogManager
							.severe(
								"["
									+ bridgeName
									+ "] Pipe overflow. Message length: "
									+ messageLength
							);
						pipe.state = PipeState.ERROR;
						return readAnything;
					}
					if (bytesAvailable.getValue() >= messageLength) {
						if (
							Kernel32.INSTANCE
								.ReadFile(
									pipe.pipeHandle,
									buffArray,
									messageLength,
									bytesAvailable,
									null
								)
						) {
							ProtobufMessage message = ProtobufMessage
								.parser()
								.parseFrom(buffArray, 4, messageLength - 4);
							messageReceived(message);
							readAnything = true;
						} else {
							pipe.state = PipeState.ERROR;
							LogManager
								.severe(
									"["
										+ bridgeName
										+ "] Pipe error: "
										+ Kernel32.INSTANCE.GetLastError()
								);
							return readAnything;
						}
					} else {
						return readAnything; // Wait for more data
					}
				} else {
					return readAnything; // Wait for more data
				}
			}
			pipe.state = PipeState.ERROR;
			LogManager
				.severe("[" + bridgeName + "] Pipe error: " + Kernel32.INSTANCE.GetLastError());
		}
		return false;
	}

	private void resetPipe() {
		WindowsPipe.safeDisconnect(pipe);
		pipe.state = PipeState.CREATED;
		Main.vrServer.queueTask(this::disconnected);
	}

	private void createPipe() throws IOException {
		try {
			pipe = new WindowsPipe(
				Kernel32.INSTANCE
					.CreateNamedPipe(
						pipeName,
						WinBase.PIPE_ACCESS_DUPLEX, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null
					),
				pipeName
			); // lpSecurityAttributes
			LogManager.info("[" + bridgeName + "] Pipe " + pipe.name + " created");
			if (WinBase.INVALID_HANDLE_VALUE.equals(pipe.pipeHandle))
				throw new IOException(
					"Can't open " + pipeName + " pipe: " + Kernel32.INSTANCE.GetLastError()
				);
			LogManager.info("[" + bridgeName + "] Pipes are created");
		} catch (IOException e) {
			WindowsPipe.safeDisconnect(pipe);
			throw e;
		}
	}

	private boolean tryOpeningPipe(WindowsPipe pipe) {
		if (
			Kernel32.INSTANCE.ConnectNamedPipe(pipe.pipeHandle, null)
				|| Kernel32.INSTANCE.GetLastError() == WinError.ERROR_PIPE_CONNECTED
		) {
			pipe.state = PipeState.OPEN;
			LogManager.info("[" + bridgeName + "] Pipe " + pipe.name + " is open");
			Main.vrServer.queueTask(this::reconnected);
			return true;
		}
		LogManager
			.info(
				"["
					+ bridgeName
					+ "] Error connecting to pipe "
					+ pipe.name
					+ ": "
					+ Kernel32.INSTANCE.GetLastError()
			);
		return false;
	}

	@Override
	public boolean isConnected() {
		return pipe != null && pipe.state == PipeState.OPEN;
	}
}
