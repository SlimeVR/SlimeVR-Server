package dev.slimevr.desktop.ipc

import com.sun.jna.Native
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinNT
import dev.slimevr.AppContextProvider
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okio.Buffer

private val k32 = Kernel32.INSTANCE
private val adv32 = Advapi32.INSTANCE

private const val PIPE_BUFFER_SIZE = 64 * 1024

private typealias ConnectionHandler = suspend (pipe: WinNT.HANDLE, writer: PipeSlot) -> Unit

suspend fun createWindowsDriverPipe(appContext: AppContextProvider) = acceptWindowsClients(DRIVER_PIPE) { pipe, writer ->
	handleDriverConnection(appContext, DriverBridgeSource.DRIVER, readFrames(pipe)) { frame -> writer.writeFrame(frame) }
}

suspend fun createWindowsFeederPipe(appContext: AppContextProvider) = acceptWindowsClients(FEEDER_PIPE) { pipe, writer ->
	handleDriverConnection(appContext, DriverBridgeSource.FEEDER, readFrames(pipe)) { frame -> writer.writeFrame(frame) }
}

suspend fun createWindowsSolarXRPipe(appContext: AppContextProvider) = acceptWindowsClients(SOLARXR_PIPE) { pipe, writer ->
	handleSolarXRBridge(appContext, readFrames(pipe)) { frame -> writer.writeFrame(frame) }
}

private fun readFrames(handle: WinNT.HANDLE) = flow {
	PipeSlot(handle).use { reader ->
		val frame = Buffer()
		while (true) {
			frame.clear()
			if (!reader.readFrameInto(frame)) break
			emit(frame)
		}
	}
}

private fun createSecurePipe(pipeName: String): WinNT.HANDLE {
	val descriptor = WinNT.SECURITY_DESCRIPTOR(64 * 1024)
	adv32.InitializeSecurityDescriptor(descriptor, WinNT.SECURITY_DESCRIPTOR_REVISION)
	adv32.SetSecurityDescriptorDacl(descriptor, true, null, false)

	val attributes = WinBase.SECURITY_ATTRIBUTES()
	attributes.lpSecurityDescriptor = descriptor.pointer
	attributes.bInheritHandle = false

	val pipe = k32.CreateNamedPipe(
		pipeName,
		WinBase.PIPE_ACCESS_DUPLEX or WinNT.FILE_FLAG_OVERLAPPED,
		WinBase.PIPE_TYPE_BYTE or WinBase.PIPE_READMODE_BYTE or WinBase.PIPE_WAIT,
		WinBase.PIPE_UNLIMITED_INSTANCES,
		PIPE_BUFFER_SIZE,
		PIPE_BUFFER_SIZE,
		0,
		attributes,
	)
	check(pipe != WinNT.INVALID_HANDLE_VALUE) {
		"CreateNamedPipe failed for $pipeName: ${Native.getLastError()}"
	}
	return pipe
}

private suspend fun acceptWindowsClients(
	pipeName: String,
	handle: ConnectionHandler,
) = withContext(Dispatchers.IO) {
	supervisorScope {
		while (isActive) {
			val pipe = createSecurePipe(pipeName)
			val writer = PipeSlot(pipe)
			val connected = try {
				writer.awaitClient()
			} catch (e: CancellationException) {
				// The pipe goes down with the driver output, so the one waiting for a client has to
				// be released here: no handler owns it yet
				writer.close()
				k32.CloseHandle(pipe)
				throw e
			}
			if (!connected) {
				writer.close()
				k32.CloseHandle(pipe)
				continue
			}
			AppLogger.ipc.info("$pipeName client connected")

			launch(Dispatchers.Default) {
				try {
					handle(pipe, writer)
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					AppLogger.ipc.error(e, "Error while handling $pipeName client, dropping connection")
				} finally {
					AppLogger.ipc.info("$pipeName client disconnected")
					writer.close()
					k32.DisconnectNamedPipe(pipe)
					k32.CloseHandle(pipe)
				}
			}
		}
	}
}
