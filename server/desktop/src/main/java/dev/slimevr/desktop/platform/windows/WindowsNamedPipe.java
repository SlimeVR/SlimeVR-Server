package dev.slimevr.desktop.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


interface Kernel32IO extends Kernel32 {
	Kernel32IO INSTANCE = Native.load("kernel32", Kernel32IO.class, W32APIOptions.DEFAULT_OPTIONS);

	boolean GetOverlappedResult(
		/* [in] */ HANDLE hFile,
		/* [in] */ WinBase.OVERLAPPED lpOverlapped,
		/* [out] */ IntByReference lpNumberOfBytesTransferred,
		/* [in] */ boolean bWait
	);
}


public class WindowsNamedPipe implements AutoCloseable {
	private static final Kernel32 k32 = Kernel32.INSTANCE;
	private static final Kernel32IO k32io = Kernel32IO.INSTANCE;
	private static final Advapi32 adv32 = Advapi32.INSTANCE;

	public interface PipeMessageReader {
		void onMessage(PipeConnection conn, ByteBuffer buf);
	}

	public static class PipeConnection implements AutoCloseable {
		private final String pipeName;
		private WinNT.HANDLE handle;
		private PipeState state;

		protected WinNT.HANDLE openEvent = k32.CreateEvent(null, false, false, null);
		protected WinNT.HANDLE readEvent = k32.CreateEvent(null, false, false, null);
		protected WinNT.HANDLE writeEvent = k32.CreateEvent(null, false, false, null);
		protected WinNT.HANDLE rxEvent = k32.CreateEvent(null, false, false, null);
		protected WinNT.HANDLE wakeEvent = k32.CreateEvent(null, false, false, null);
		protected WinNT.HANDLE[] events = new WinNT.HANDLE[] { openEvent, rxEvent, wakeEvent };
		private final WinBase.OVERLAPPED overlappedOpen = new WinBase.OVERLAPPED();
		private final WinBase.OVERLAPPED overlappedWrite = new WinBase.OVERLAPPED();
		private final WinBase.OVERLAPPED overlappedRead = new WinBase.OVERLAPPED();
		private final WinBase.OVERLAPPED overlappedWait = new WinBase.OVERLAPPED();
		private final ByteBuffer buf = ByteBuffer.allocate(2048);
		private final IntByReference bytesWritten = new IntByReference(0);
		private final IntByReference bytesAvailable = new IntByReference(0);
		private final IntByReference bytesRead = new IntByReference(0);
		private boolean pendingWait = false;

		PipeConnection(String pipeName, WinNT.HANDLE pipeHandle) {
			this.pipeName = pipeName;
			this.handle = pipeHandle;
			this.state = PipeState.CREATED;

			// Kick off pipe connection.
			tryConnect();
		}

		protected void tryConnect() {
			overlappedOpen.clear();
			overlappedOpen.hEvent = openEvent;

			boolean ok = k32.ConnectNamedPipe(handle, overlappedOpen);
			int err = k32.GetLastError();
			if (!ok && err != WinError.ERROR_IO_PENDING) {
				if (!k32io.GetOverlappedResult(handle, overlappedOpen, bytesRead, true)) {
					setError("ConnectNamedPipe failed: " + err);
				}
			}
		}

		// Returns true when:
		// Pipe has just been opened.
		// Pipe is open and we got data.
		public boolean update(PipeMessageReader reader) {
			switch (state) {
				case ERROR: {
					// Do nothing.
					return false;
				}
				case CREATED: {
					// Check if any events are signaled.
					int evIdx = k32.WaitForMultipleObjects(events.length, events, false, 0);
					if (evIdx == WinError.WAIT_TIMEOUT) {
						// Do nothing if timed out.
						return false;
					}
					if (evIdx == WinBase.WAIT_FAILED) {
						setError("Wait failed: " + k32.GetLastError());
					}

					// Did open succeed?
					if (evIdx == 0) {
						LogManager
							.info("[WindowsNamedPipeConnection] Pipe " + pipeName + " opened");
						state = PipeState.OPEN;
						return true;
					}
					break;
				}
				case OPEN: {
					// Did we get data?
					if (waitForData(0)) {
						return tryRead(reader);
					}
					break;
				}
			}
			return false;
		}

		@Override
		public void close() {
			if (state == PipeState.OPEN)
				disconnect();

			if (wakeEvent != null) {
				k32.CloseHandle(wakeEvent);
				wakeEvent = null;
			}
			if (rxEvent != null) {
				k32.CloseHandle(rxEvent);
				rxEvent = null;
			}
			if (writeEvent != null) {
				k32.CloseHandle(writeEvent);
				writeEvent = null;
			}
			if (readEvent != null) {
				k32.CloseHandle(readEvent);
				readEvent = null;
			}
			if (openEvent != null) {
				k32.CloseHandle(openEvent);
				openEvent = null;
			}
			if (handle != null) {
				k32.CloseHandle(handle);
				handle = null;
			}
		}

		public void setError(String err) {
			pendingWait = false;
			state = PipeState.ERROR;
			LogManager
				.severe("[WindowsNamedPipeConnection] Error on pipe " + pipeName + ": " + err);
		}

		// Wake up immediately from waitForData.
		// Could be used by the caller to pump a message queue immediately.
		public void wakeUp() {
			k32.SetEvent(wakeEvent);
		}

		// Try to wait timeoutMs for something to be written to the pipe.
		public boolean waitForData(int timeoutMs) {
			if (state != PipeState.OPEN)
				return false;
			if (!pendingWait) {
				overlappedWait.clear();
				overlappedWait.hEvent = rxEvent;
				boolean immediate = k32.ReadFile(handle, null, 0, null, overlappedWait);
				int err = k32.GetLastError();
				if (!immediate && err != WinError.ERROR_IO_PENDING) {
					if (err == WinError.ERROR_BROKEN_PIPE || err == WinError.ERROR_NO_DATA) {
						setError("Pipe closed");
					} else {
						setError("ReadFile failed: " + err);
					}
					return false;
				}
				pendingWait = true;
			}

			// Did we get data?
			int evIdx = k32.WaitForMultipleObjects(events.length, events, false, timeoutMs);
			if (evIdx == 1) {
				// events[0] == overlappedWait.hEvent == rxEvent
				pendingWait = false;
				return true;
			}
			return false;
		}

		// Send a message to the pipe.
		public boolean sendBuffer(byte[] buffer, int len) {
			overlappedWrite.clear();
			overlappedWrite.hEvent = writeEvent;
			boolean immediate = k32
				.WriteFile(handle, buffer, len, null, overlappedWrite);
			int err = k32.GetLastError();
			if (!immediate && err != WinError.ERROR_IO_PENDING) {
				setError("WriteFile failed: " + err);
				return false;
			}

			if (!k32io.GetOverlappedResult(handle, overlappedWrite, bytesWritten, true)) {
				setError(
					"sendMessageReal/GetOverlappedResult failed: " + k32.GetLastError()
				);
				return false;
			}

			if (bytesWritten.getValue() != len) {
				setError("Bytes written " + bytesWritten.getValue() + ", expected " + len);
				return false;
			}

			return true;
		}

		// Try to read messages from the buffer into reader.
		protected boolean tryRead(PipeMessageReader reader) {
			if (state != PipeState.OPEN) {
				return false;
			}

			boolean anyReceived = false;
			buf.clear();
			while (k32.PeekNamedPipe(handle, buf.array(), 4, null, bytesAvailable, null)) {
				if (bytesAvailable.getValue() < 4) {
					return anyReceived; // Wait for more data
				}
				buf.position(0);
				buf.order(ByteOrder.LITTLE_ENDIAN);
				int messageLength = buf.getInt();
				if (messageLength > 1024) { // Overflow
					setError("Pipe overflow. Message length: " + messageLength);
					return anyReceived;
				}
				if (bytesAvailable.getValue() < messageLength) {
					return anyReceived; // Wait for more data
				}

				overlappedRead.clear();
				overlappedRead.hEvent = readEvent;
				buf.position(0);
				boolean immediate = k32
					.ReadFile(handle, buf.array(), messageLength, null, overlappedRead);
				int err = k32.GetLastError();
				if (!immediate && err != WinError.ERROR_IO_PENDING) {
					setError("ReadFile failed: " + err);
					return anyReceived;
				}

				if (!k32io.GetOverlappedResult(handle, overlappedRead, bytesRead, true)) {
					setError(
						"updatePipe/GetOverlappedResult failed: " + k32.GetLastError()
					);
					return anyReceived;
				}

				if (bytesRead.getValue() != messageLength) {
					setError(
						"Bytes read " + bytesRead.getValue() + ", expected " + messageLength
					);
					return anyReceived;
				}

				buf.clear();
				buf.position(4); // skip size
				buf.limit(messageLength);
				reader.onMessage(this, buf);
				anyReceived = true;

				if (state != PipeState.OPEN)
					return anyReceived;
			}

			int err = k32.GetLastError();
			if (err == WinError.ERROR_BROKEN_PIPE) {
				setError("Pipe closed");
			} else {
				setError("Pipe error: " + err);
			}
			return anyReceived;
		}

		public void disconnect() {
			if (!k32.DisconnectNamedPipe(handle)) {
				LogManager
					.warning(
						"[WindowsNamedPipeConnection] Disconnect failed: " + k32.GetLastError()
					);
			}

			state = PipeState.CREATED;
			tryConnect();
		}

		public PipeState getState() {
			return state;
		}
	}

	protected final String pipeName;
	private final PipeConnection[] pipeConnections;

	public WindowsNamedPipe(String pipeName, int maxConnections) throws IOException {
		this.pipeName = pipeName;
		this.pipeConnections = new PipeConnection[maxConnections];

		try {
			WinNT.SECURITY_DESCRIPTOR descriptor = new WinNT.SECURITY_DESCRIPTOR(64 * 1024);
			adv32.InitializeSecurityDescriptor(descriptor, WinNT.SECURITY_DESCRIPTOR_REVISION);
			adv32.SetSecurityDescriptorDacl(descriptor, true, null, false);
			adv32
				.SetSecurityDescriptorControl(
					descriptor,
					(short) WinNT.SE_DACL_PROTECTED,
					(short) WinNT.SE_DACL_PROTECTED
				);

			WinBase.SECURITY_ATTRIBUTES attributes = new WinBase.SECURITY_ATTRIBUTES();
			attributes.lpSecurityDescriptor = descriptor.getPointer();
			attributes.bInheritHandle = false;

			for (int i = 0; i < maxConnections; i++) {
				WinNT.HANDLE pipeHandle = k32
					.CreateNamedPipe(
						pipeName,
						WinBase.PIPE_ACCESS_DUPLEX | WinNT.FILE_FLAG_OVERLAPPED, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE | WinBase.PIPE_READMODE_BYTE | WinBase.PIPE_WAIT, // dwPipeMode
						maxConnections, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						attributes // lpSecurityAttributes
					);
				if (WinBase.INVALID_HANDLE_VALUE.equals(pipeHandle)) {
					throw new IOException(
						"Failed to open " + pipeName + " pipe: " + k32.GetLastError()
					);
				}
				this.pipeConnections[i] = new PipeConnection(pipeName, pipeHandle);
			}
			LogManager.info("[WindowsNamedPipe] Pipe " + pipeName + " created");

		} catch (IOException e) {
			for (PipeConnection conn : this.pipeConnections) {
				if (conn != null)
					conn.close();
			}
			throw e;
		}
	}

	public PipeConnection tryAccept() {
		for (PipeConnection conn : this.pipeConnections) {
			if (conn.getState() == PipeState.CREATED && conn.update(null)) {
				// We got the pipe.
				return conn;
			}
		}
		return null;
	}

	@Override
	public void close() {
		for (PipeConnection pipeConnection : this.pipeConnections) {
			pipeConnection.close();
		}
	}
}
