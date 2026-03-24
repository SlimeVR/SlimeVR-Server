package dev.slimevr.desktop.ipc

import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinError
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.ptr.IntByReference
import dev.slimevr.VRServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

private val k32 = Kernel32.INSTANCE
private val adv32 = Advapi32.INSTANCE

suspend fun createWindowsDriverPipe(server: VRServer) =
	acceptWindowsClients(DRIVER_PIPE) { handle ->
		handleDriverConnection(
			server = server,
			messages = readFramedMessages(handle),
			send = { bytes -> withContext(Dispatchers.IO) { writeFramedPipe(handle, bytes) } },
		)
	}

suspend fun createWindowsFeederPipe(server: VRServer) =
	acceptWindowsClients(FEEDER_PIPE) { handle ->
		handleFeederConnection(
			server = server,
			messages = readFramedMessages(handle),
			send = { bytes -> withContext(Dispatchers.IO) { writeFramedPipe(handle, bytes) } },
		)
	}

suspend fun createWindowsSolarXRPipe(server: VRServer) =
	acceptWindowsClients(SOLARXR_PIPE) { handle ->
		handleSolarXRConnection(
			server = server,
			messages = readFramedMessages(handle),
			send = { bytes -> withContext(Dispatchers.IO) { writeFramedPipe(handle, bytes) } },
		)
	}

// Length field is LE u32 and includes the 4-byte header itself
private fun readFramedMessages(handle: WinNT.HANDLE) = flow {
	val lenBuf = ByteArray(4)
	while (true) {
		if (!readExact(handle, lenBuf, 4)) break
		val totalLen = ByteBuffer.wrap(lenBuf).order(ByteOrder.LITTLE_ENDIAN).int

		val dataBuf = ByteArray(totalLen - 4)
		if (!readExact(handle, dataBuf, totalLen - 4)) break
		emit(dataBuf)
	}
}.flowOn(Dispatchers.IO)

private fun readExact(handle: WinNT.HANDLE, buf: ByteArray, len: Int): Boolean {
	var offset = 0
	val bytesRead = IntByReference()
	while (offset < len) {
		val ok = k32.ReadFile(handle, buf, len - offset, bytesRead, null)
		if (!ok || bytesRead.value == 0) return false
		offset += bytesRead.value
	}
	return true
}

private fun writeFramedPipe(handle: WinNT.HANDLE, bytes: ByteArray) {
	val buf = ByteArray(bytes.size + 4)
	ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.size + 4)
	bytes.copyInto(buf, destinationOffset = 4)
	k32.WriteFile(handle, buf, buf.size, IntByReference(), null)
}

private fun createSecurePipe(pipeName: String): WinNT.HANDLE {
	// Null DACL allows any process (including SteamVR driver) to connect
	val descriptor = WinNT.SECURITY_DESCRIPTOR(64 * 1024)
	adv32.InitializeSecurityDescriptor(descriptor, WinNT.SECURITY_DESCRIPTOR_REVISION)
	adv32.SetSecurityDescriptorDacl(descriptor, true, null, false)

	val attributes = WinBase.SECURITY_ATTRIBUTES()
	attributes.lpSecurityDescriptor = descriptor.pointer
	attributes.bInheritHandle = false

	val pipe = k32.CreateNamedPipe(
		pipeName,
		WinBase.PIPE_ACCESS_DUPLEX,
		WinBase.PIPE_TYPE_BYTE or WinBase.PIPE_READMODE_BYTE or WinBase.PIPE_WAIT,
		WinBase.PIPE_UNLIMITED_INSTANCES,
		65536, 65536, 0, attributes,
	)
	check(pipe != WinNT.INVALID_HANDLE_VALUE) {
		"CreateNamedPipe failed for $pipeName: ${k32.GetLastError()}"
	}
	return pipe
}

private suspend fun acceptWindowsClients(
	pipeName: String,
	handle: suspend (WinNT.HANDLE) -> Unit,
) = withContext(Dispatchers.IO) {
	while (isActive) {
		val pipe = createSecurePipe(pipeName)

		val ok = k32.ConnectNamedPipe(pipe, null)
		val err = k32.GetLastError()
		if (!ok && err != WinError.ERROR_PIPE_CONNECTED) {
			k32.CloseHandle(pipe)
			continue
		}

		launch {
			try {
				handle(pipe)
			} finally {
				k32.DisconnectNamedPipe(pipe)
				k32.CloseHandle(pipe)
			}
		}
	}
}