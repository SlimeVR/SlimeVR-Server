package dev.slimevr.desktop.platform.windows

import com.google.protobuf.CodedOutputStream
import com.google.protobuf.InvalidProtocolBufferException
import com.sun.jna.Native
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinError
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.W32APIOptions
import dev.slimevr.VRServer
import dev.slimevr.bridge.BridgeThread
import dev.slimevr.desktop.platform.ProtobufMessages.ProtobufMessage
import dev.slimevr.desktop.platform.SteamVRBridge
import dev.slimevr.tracking.trackers.Tracker
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.lang.Byte
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Throws
import kotlin.arrayOf

internal interface Kernel32IO : Kernel32 {
	@Suppress("FunctionName")
	fun GetOverlappedResult(
		hFile: WinNT.HANDLE?, /* [in] */
		lpOverlapped: WinBase.OVERLAPPED?, /* [in] */
		lpNumberOfBytesTransferred: IntByReference?, /* [out] */
		bWait: Boolean, /* [in] */
	): Boolean

	companion object {
		val INSTANCE: Kernel32IO =
			Native.load("kernel32", Kernel32IO::class.java, W32APIOptions.DEFAULT_OPTIONS)
	}
}

enum class PipeState {
	CREATED,
	OPEN,
	ERROR,
}
class WindowsPipe(val pipeHandle: WinNT.HANDLE?, val name: String) {
	var state: PipeState = PipeState.CREATED

	fun safeDisconnect() {
		if (pipeHandle != null && pipeHandle != WinBase.INVALID_HANDLE_VALUE) {
			Kernel32.INSTANCE.DisconnectNamedPipe(pipeHandle)
		}
	}
}

class WindowsNamedPipeBridge(
	server: VRServer,
	bridgeSettingsKey: String,
	bridgeName: String,
	private val pipeName: String,
	shareableTrackers: List<Tracker>,
) : SteamVRBridge(server, "Named pipe thread", bridgeName, bridgeSettingsKey, shareableTrackers) {
	lateinit var pipe: WindowsPipe
	private val buffArray = ByteArray(2048)
	private val openEvent: WinNT.HANDLE? = k32.CreateEvent(null, false, false, null)
	private val readEvent: WinNT.HANDLE? = k32.CreateEvent(null, false, false, null)
	private val writeEvent: WinNT.HANDLE? = k32.CreateEvent(null, false, false, null)
	private val rxEvent: WinNT.HANDLE? = k32.CreateEvent(null, false, false, null)
	private val txEvent: WinNT.HANDLE? = k32.CreateEvent(null, false, false, null)
	private val events = arrayOf(rxEvent, txEvent)
	private val overlappedOpen = WinBase.OVERLAPPED()
	private val overlappedWrite = WinBase.OVERLAPPED()
	private val overlappedRead = WinBase.OVERLAPPED()
	private val overlappedWait = WinBase.OVERLAPPED()
	private val bytesWritten = IntByReference(0)
	private val bytesAvailable = IntByReference(0)
	private val bytesRead = IntByReference(0)
	private var pendingWait = false

	init {
		overlappedWait.hEvent = rxEvent
	}

	@BridgeThread
	override fun run() {
		try {
			createPipe()
			while (true) {
				var pipesUpdated = false
				if (pipe.state == PipeState.CREATED) {
					// Report that our pipe is disconnected right now
					reportDisconnected()
					tryOpeningPipe(pipe)
				}
				if (pipe.state == PipeState.OPEN) {
					pipesUpdated = updatePipe()
					if (pipesUpdated) {
						reportConnected()
					}
					updateMessageQueue()
				}
				if (pipe.state == PipeState.ERROR) {
					resetPipe()
				}
				if (!pipesUpdated) {
					if (pipe.state == PipeState.OPEN) {
						waitForData(10)
					} else {
						try {
							Thread.sleep(10)
						} catch (_: InterruptedException) {
							Thread.currentThread().interrupt()
						}
					}
				}
			}
		} catch (e: IOException) {
			LogManager.severe("[$bridgeName] Exception while running bridge", e)
		}
	}

	@ThreadSafe
	override fun signalSend() {
		k32.SetEvent(txEvent)
	}

	@BridgeThread
	private fun waitForData(timeoutMs: Int) {
		if (pipe.state != PipeState.OPEN) return
		if (!pendingWait) {
			k32.ReadFile(pipe.pipeHandle, null, 0, null, overlappedWait)
			pendingWait = true
		}
		val evIdx: Int = k32.WaitForMultipleObjects(events.size, events, false, timeoutMs)
		if (evIdx == 0) {
			// events[0] == overlappedWait.hEvent == rxEvent
			pendingWait = false
		}
	}

	@BridgeThread
	override fun sendMessageReal(message: ProtobufMessage): Boolean {
		if (pipe.state != PipeState.OPEN) return false
		try {
			var size = message.getSerializedSize()
			val os = CodedOutputStream.newInstance(buffArray, 4, size)
			message.writeTo(os)
			size += 4
			buffArray[0] = (size and 0xFF).toByte()
			buffArray[1] = ((size shr 8) and 0xFF).toByte()
			buffArray[2] = ((size shr 16) and 0xFF).toByte()
			buffArray[3] = ((size shr 24) and 0xFF).toByte()

			overlappedWrite.clear()
			overlappedWrite.hEvent = writeEvent
			val immediate: Boolean = k32
				.WriteFile(pipe.pipeHandle, buffArray, size, null, overlappedWrite)
			val err: Int = k32.GetLastError()
			if (!immediate && err != WinError.ERROR_IO_PENDING) {
				setPipeError("WriteFile failed: $err")
				return false
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedWrite, bytesWritten, true)) {
				setPipeError("sendMessageReal/GetOverlappedResult failed: ${k32.GetLastError()}")
				return false
			}

			if (bytesWritten.value != size) {
				setPipeError("Bytes written ${bytesWritten.value}, expected $size")
				return false
			}

			return true
		} catch (e: IOException) {
			LogManager.severe("[$bridgeName] Failed to send message", e)
		}
		return false
	}

	@Throws(IOException::class)
	private fun updatePipe(): Boolean {
		if (pipe.state != PipeState.OPEN) {
			return false
		}
		var readAnything = false
		while (k32.PeekNamedPipe(pipe.pipeHandle, buffArray, 4, null, bytesAvailable, null)) {
			if (bytesAvailable.value < 4) {
				return readAnything // Wait for more data
			}
			val messageLength = (
				(Byte.toUnsignedInt(buffArray[3]) shl 24)
					or (Byte.toUnsignedInt(buffArray[2]) shl 16)
					or (Byte.toUnsignedInt(buffArray[1]) shl 8)
					or Byte.toUnsignedInt(buffArray[0])
				)
			if (messageLength > 1024) { // Overflow
				setPipeError("Pipe overflow. Message length: $messageLength")
				return readAnything
			}
			if (bytesAvailable.value < messageLength) {
				return readAnything // Wait for more data
			}

			overlappedRead.clear()
			overlappedRead.hEvent = readEvent
			val immediate: Boolean = k32
				.ReadFile(pipe.pipeHandle, buffArray, messageLength, null, overlappedRead)
			val err: Int = k32.GetLastError()
			if (!immediate && err != WinError.ERROR_IO_PENDING) {
				setPipeError("ReadFile failed: $err")
				return readAnything
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedRead, bytesRead, true)) {
				setPipeError("updatePipe/GetOverlappedResult failed: ${k32.GetLastError()}")
				return readAnything
			}

			if (bytesRead.value != messageLength) {
				setPipeError("Bytes read ${bytesRead.value}, expected $messageLength")
				return readAnything
			}

			try {
				val message = ProtobufMessage
					.parser()
					.parseFrom(buffArray, 4, messageLength - 4)
				messageReceived(message)
				readAnything = true
			} catch (e: InvalidProtocolBufferException) {
				setPipeError("Failed to parse message: ${e.message}")
				e.printStackTrace()
				return readAnything
			}
		}

		val err = k32.GetLastError()
		if (err == WinError.ERROR_BROKEN_PIPE) {
			setPipeError("Pipe closed")
		} else {
			setPipeError("Pipe error: $err")
		}
		return readAnything
	}

	private fun setPipeError(message: String) {
		pipe.state = PipeState.ERROR
		LogManager.severe("[$bridgeName] $message")
	}

	private fun resetPipe() {
		pipe.safeDisconnect()
		pipe.state = PipeState.CREATED
		server.queueTask { this.disconnected() }
	}

	@Throws(IOException::class)
	private fun createPipe() {
		try {
			val descriptor = WinNT.SECURITY_DESCRIPTOR(64 * 1024)
			adv32.InitializeSecurityDescriptor(descriptor, WinNT.SECURITY_DESCRIPTOR_REVISION)
			adv32.SetSecurityDescriptorDacl(descriptor, true, null, false)
			adv32
				.SetSecurityDescriptorControl(
					descriptor,
					WinNT.SE_DACL_PROTECTED.toShort(),
					WinNT.SE_DACL_PROTECTED.toShort(),
				)

			val attributes = WinBase.SECURITY_ATTRIBUTES()
			attributes.lpSecurityDescriptor = descriptor.pointer
			attributes.bInheritHandle = false

			pipe = WindowsPipe(
				k32
					.CreateNamedPipe(
						pipeName,
						WinBase.PIPE_ACCESS_DUPLEX or WinNT.FILE_FLAG_OVERLAPPED, // dwOpenMode
						WinBase.PIPE_TYPE_BYTE or WinBase.PIPE_READMODE_BYTE or WinBase.PIPE_WAIT, // dwPipeMode
						1, // nMaxInstances,
						1024 * 16, // nOutBufferSize,
						1024 * 16, // nInBufferSize,
						0, // nDefaultTimeOut,
						attributes, // lpSecurityAttributes
					),
				pipeName,
			)
			LogManager.info("[$bridgeName] Pipe ${pipe.name} created")
			if (WinBase.INVALID_HANDLE_VALUE == pipe.pipeHandle) {
				throw IOException("Can't open $pipeName pipe: ${k32.GetLastError()}")
			}
			LogManager.info("[$bridgeName] Pipes are created")
		} catch (e: IOException) {
			pipe.safeDisconnect()
			throw e
		}
	}

	private fun tryOpeningPipe(pipe: WindowsPipe): Boolean {
		overlappedOpen.clear()
		overlappedOpen.hEvent = openEvent

		val ok = k32.ConnectNamedPipe(pipe.pipeHandle, overlappedOpen)
		val err = k32.GetLastError()
		if (!ok && err != WinError.ERROR_PIPE_CONNECTED) {
			if (err != WinError.ERROR_IO_PENDING) {
				setPipeError("ConnectNamedPipe failed: $err")
				return false
			}

			if (!k32io.GetOverlappedResult(pipe.pipeHandle, overlappedOpen, bytesRead, true)) {
				setPipeError("tryOpeningPipe/GetOverlappedResult failed: ${k32.GetLastError()}")
				return false
			}
		}

		pipe.state = PipeState.OPEN
		LogManager.info("[$bridgeName] Pipe ${pipe.name} is open")
		server.queueTask { this.reconnected() }
		return true
	}

	override fun isConnected() = pipe.state == PipeState.OPEN

	override fun getBridgeConfigKey() = this.bridgeSettingsKey

	companion object {
		private val k32: Kernel32 = Kernel32.INSTANCE
		private val k32io: Kernel32IO = Kernel32IO.INSTANCE
		private val adv32: Advapi32 = Advapi32.INSTANCE
	}
}
