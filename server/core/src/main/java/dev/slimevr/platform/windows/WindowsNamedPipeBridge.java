package dev.slimevr.platform.windows;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.tracking.trackers.Tracker;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.util.List;


interface Kernel32IO extends Kernel32 {
	Kernel32IO INSTANCE = Native.load("kernel32", Kernel32IO.class, W32APIOptions.DEFAULT_OPTIONS);

	boolean GetOverlappedResult(
		/* [in] */ HANDLE hFile,
		/* [in] */ WinBase.OVERLAPPED lpOverlapped,
		/* [out] */ IntByReference lpNumberOfBytesTransferred,
		/* [in] */ boolean bWait
	);
}


public class WindowsNamedPipeBridge extends SteamVRBridge {
	private static final Kernel32 k32 = Kernel32.INSTANCE;
	private static final Kernel32IO k32io = Kernel32IO.INSTANCE;

	protected final String pipeName;
	private final byte[] buffArray = new byte[2048];
	protected WindowsPipe pipe;
	protected WinNT.HANDLE openEvent = k32.CreateEvent(null, false, false, null);
	protected WinNT.HANDLE readEvent = k32.CreateEvent(null, false, false, null);
	protected WinNT.HANDLE writeEvent = k32.CreateEvent(null, false, false, null);
	protected WinNT.HANDLE rxEvent = k32.CreateEvent(null, false, false, null);
	protected WinNT.HANDLE txEvent = k32.CreateEvent(null, false, false, null);
	protected WinNT.HANDLE[] events = new WinNT.HANDLE[] { rxEvent, txEvent };
	private final WinBase.OVERLAPPED overlappedOpen = new WinBase.OVERLAPPED();
	private final WinBase.OVERLAPPED overlappedWrite = new WinBase.OVERLAPPED();
	private final WinBase.OVERLAPPED overlappedRead = new WinBase.OVERLAPPED();
	private final WinBase.OVERLAPPED overlappedWait = new WinBase.OVERLAPPED();
	private final IntByReference bytesWritten = new IntByReference(0);
	private final IntByReference bytesAvailable = new IntByReference(0);
	private final IntByReference bytesRead = new IntByReference(0);
	private boolean pendingWait = false;

	public WindowsNamedPipeBridge(
		VRServer server,
		Tracker hmd,
		String bridgeSettingsKey,
		String bridgeName,
		String pipeName,
		List<Tracker> shareableTrackers
	) {
		super(server, hmd, "Named pipe thread", bridgeName, bridgeSettingsKey, shareableTrackers);
		this.pipeName = pipeName;
		overlappedWait.hEvent = rxEvent;
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			createPipe();
			while (true) {
				boolean pipesUpdated = false;
				if (pipe.state == PipeState.CREATED) {
					// Report that our pipe is disconnected right now
					reportDisconnected();
					tryOpeningPipe(pipe);
				}
				if (pipe.state == PipeState.OPEN) {
					pipesUpdated = updatePipe();
					if (lastSteamVRStatus != 0 && pipesUpdated) {
						Main.getVrServer().getStatusSystem().removeStatusInt(lastSteamVRStatus);
						lastSteamVRStatus = 0;
					}
					updateMessageQueue();
				}
				if (pipe.state == PipeState.ERROR) {
					resetPipe();
				}
				if (!pipesUpdated) {
					if (pipe.state == PipeState.OPEN) {
						waitForData(10);
					} else {
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
		k32.SetEvent(txEvent);
	}

	@BridgeThread
	private void waitForData(int timeoutMs) {
		if (pipe.state != PipeState.OPEN)
			return;
		if (!pendingWait) {
			k32.ReadFile(pipe.pipeHandle, null, 0, null, overlappedWait);
			pendingWait = true;
		}
		int evIdx = k32.WaitForMultipleObjects(events.length, events, false, timeoutMs);
		if (evIdx == 0) {
			// events[0] == overlappedWait.hEvent == rxEvent
			pendingWait = false;
		}
	}

	@Override
	@BridgeThread
	protected boolean sendMessageReal(ProtobufMessage message) {
		if (pipe.state != PipeState.OPEN) {
			return false;
		}
		try {
			int size = message.getSerializedSize();
			CodedOutputStream os = CodedOutputStream.newInstance(buffArray, 4, size);
			message.writeTo(os);
			size += 4;
			buffArray[0] = (byte) (size & 0xFF);
			buffArray[1] = (byte) ((size >> 8) & 0xFF);
			buffArray[2] = (byte) ((size >> 16) & 0xFF);
			buffArray[3] = (byte) ((size >> 24) & 0xFF);

			overlappedWrite.clear();
			overlappedWrite.hEvent = writeEvent;
			boolean immediate = k32
				.WriteFile(pipe.pipeHandle, buffArray, size, null, overlappedWrite);
			int err = k32.GetLastError();
			if (!immediate && err != WinError.ERROR_IO_PENDING) {
				setPipeError("WriteFile failed: " + err);
				return false;
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedWrite, bytesWritten, true)) {
				setPipeError(
					"sendMessageReal/GetOverlappedResult failed: " + k32.GetLastError()
				);
				return false;
			}

			if (bytesWritten.getValue() != size) {
				setPipeError("Bytes written " + bytesWritten.getValue() + ", expected " + size);
				return false;
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean updatePipe() throws IOException {
		if (pipe.state != PipeState.OPEN) {
			return false;
		}
		boolean readAnything = false;
		while (k32.PeekNamedPipe(pipe.pipeHandle, buffArray, 4, null, bytesAvailable, null)) {
			if (bytesAvailable.getValue() < 4) {
				return readAnything; // Wait for more data
			}
			int messageLength = (buffArray[3] << 24)
				| (buffArray[2] << 16)
				| (buffArray[1] << 8)
				| buffArray[0];
			if (messageLength > 1024) { // Overflow
				setPipeError("Pipe overflow. Message length: " + messageLength);
				return readAnything;
			}
			if (bytesAvailable.getValue() < messageLength) {
				return readAnything; // Wait for more data
			}

			overlappedRead.clear();
			overlappedRead.hEvent = readEvent;
			boolean immediate = k32
				.ReadFile(pipe.pipeHandle, buffArray, messageLength, null, overlappedRead);
			int err = k32.GetLastError();
			if (!immediate && err != WinError.ERROR_IO_PENDING) {
				setPipeError("ReadFile failed: " + err);
				return readAnything;
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedRead, bytesRead, true)) {
				setPipeError(
					"updatePipe/GetOverlappedResult failed: " + k32.GetLastError()
				);
				return readAnything;
			}

			if (bytesRead.getValue() != messageLength) {
				setPipeError(
					"Bytes read " + bytesRead.getValue() + ", expected " + messageLength
				);
				return readAnything;
			}

			try {
				ProtobufMessage message = ProtobufMessage
					.parser()
					.parseFrom(buffArray, 4, messageLength - 4);
				messageReceived(message);
				readAnything = true;
			} catch (InvalidProtocolBufferException parseEx) {
				parseEx.printStackTrace();
				setPipeError("Failed to parse message: " + parseEx.getMessage());
				return readAnything;
			}
		}

		int err = k32.GetLastError();
		if (err == WinError.ERROR_BROKEN_PIPE) {
			setPipeError("Pipe closed");
		} else {
			setPipeError("Pipe error: " + err);
		}
		return readAnything;
	}

	private void setPipeError(String message) {
		pipe.state = PipeState.ERROR;
		LogManager.severe("[" + bridgeName + "] " + message);
	}

	private void resetPipe() {
		WindowsPipe.safeDisconnect(pipe);
		pipe.state = PipeState.CREATED;
		Main.getVrServer().queueTask(this::disconnected);
	}

	private void createPipe() throws IOException {
		try {
			pipe = new WindowsPipe(
				k32
					.CreateNamedPipe(
						pipeName,
						WinBase.PIPE_ACCESS_DUPLEX | WinNT.FILE_FLAG_OVERLAPPED, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						null // lpSecurityAttributes
					),
				pipeName
			);
			LogManager.info("[" + bridgeName + "] Pipe " + pipe.name + " created");
			if (WinBase.INVALID_HANDLE_VALUE.equals(pipe.pipeHandle)) {
				throw new IOException("Can't open " + pipeName + " pipe: " + k32.GetLastError());
			}
			LogManager.info("[" + bridgeName + "] Pipes are created");
		} catch (IOException e) {
			WindowsPipe.safeDisconnect(pipe);
			throw e;
		}
	}

	private boolean tryOpeningPipe(WindowsPipe pipe) {
		overlappedOpen.clear();
		overlappedOpen.hEvent = openEvent;

		boolean ok = k32.ConnectNamedPipe(pipe.pipeHandle, overlappedOpen);
		int err = k32.GetLastError();
		if (!ok && err != WinError.ERROR_PIPE_CONNECTED) {
			if (err != WinError.ERROR_IO_PENDING) {
				setPipeError("ConnectNamedPipe failed: " + err);
				return false;
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedOpen, bytesRead, true)) {
				setPipeError(
					"tryOpeningPipe/GetOverlappedResult failed: " + k32.GetLastError()
				);
				return false;
			}
		}

		pipe.state = PipeState.OPEN;
		LogManager.info("[" + bridgeName + "] Pipe " + pipe.name + " is open");
		Main.getVrServer().queueTask(this::reconnected);
		return true;
	}

	@Override
	public boolean isConnected() {
		return pipe != null && pipe.state == PipeState.OPEN;
	}
}
