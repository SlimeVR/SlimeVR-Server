package dev.slimevr.desktop.ipc

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinError
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.win32.StdCallLibrary
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okio.Buffer
import java.io.IOException

private val k32 = Kernel32.INSTANCE

private const val HEADER_SIZE = 4

private const val WAIT_SLICE_MILLIS = 200
private const val WAIT_TIMEOUT = 0x102

@Suppress("FunctionName")
private object Kernel32IO : StdCallLibrary {
	init {
		Native.register(Kernel32IO::class.java, "kernel32")
	}

	external fun ReadFile(handle: Pointer, buffer: Pointer, len: Int, read: Pointer?, overlapped: Pointer): Boolean

	external fun WriteFile(handle: Pointer, buffer: Pointer, len: Int, written: Pointer?, overlapped: Pointer): Boolean

	external fun GetOverlappedResult(handle: Pointer, overlapped: Pointer, moved: Pointer, wait: Boolean): Boolean

	external fun WaitForSingleObject(handle: Pointer, millis: Int): Int

	external fun CancelIoEx(handle: Pointer, overlapped: Pointer?): Boolean
}

/**
 * One slot of overlapped I/O on a Windows pipe handle: an event, an [WinBase.OVERLAPPED] and a pinned
 * buffer.
 */
internal class PipeSlot(private val handle: WinNT.HANDLE) : AutoCloseable {
	private val event = checkNotNull(k32.CreateEvent(null, true, false, null)) {
		"CreateEvent failed: ${Native.getLastError()}"
	}

	private val overlapped = WinBase.OVERLAPPED().apply {
		hEvent = event
		write()
		setAutoSynch(false)
	}

	private val buffer = Memory(MAX_FRAME_SIZE.toLong())

	// Staging area between the pinned native buffer and okio, reused so a frame costs no allocation
	private val scratch = ByteArray(MAX_FRAME_SIZE)

	// Resolved once. The wrappers have to stay referenced to keep the native memory alive anyway, so
	// unwrapping them per call would only re-do work. `transferred` replaces an IntByReference that
	// was allocated on every completion.
	private val handlePointer = handle.pointer
	private val eventPointer = event.pointer
	private val overlappedPointer = overlapped.pointer
	private val transferred = Memory(4)

	suspend fun awaitClient(): Boolean {
		// On an overlapped handle ConnectNamedPipe returns straight away, so we do the waiting
		val connected = k32.ConnectNamedPipe(handle, overlapped)
		val err = Native.getLastError()
		return when {
			// ERROR_PIPE_CONNECTED means the client got in before we asked
			connected || err == WinError.ERROR_PIPE_CONNECTED -> true

			err == WinError.ERROR_IO_PENDING -> awaitCompletion() >= 0

			else -> false
		}
	}

	// Returns false once the peer closed. The overlapped wait blocks the thread it runs on, so both
	// sides of the pipe stay on Dispatchers.IO
	suspend fun readFrameInto(out: Buffer): Boolean = withContext(Dispatchers.IO) {
		if (!readInto(0, HEADER_SIZE)) return@withContext false
		val total = buffer.getInt(0)
		if (total !in HEADER_SIZE..MAX_FRAME_SIZE) throw IOException("Frame length out of range: $total")
		if (!readInto(HEADER_SIZE, total - HEADER_SIZE)) return@withContext false

		val payload = total - HEADER_SIZE
		buffer.read(HEADER_SIZE.toLong(), scratch, 0, payload)
		out.write(scratch, 0, payload)
		true
	}

	suspend fun writeFrame(payload: Buffer) {
		val size = payload.size.toInt()
		val total = size + HEADER_SIZE
		if (total > MAX_FRAME_SIZE) throw IOException("Frame too large to send: $total bytes")
		payload.read(scratch, 0, size)
		buffer.setInt(0, total)
		buffer.write(HEADER_SIZE.toLong(), scratch, 0, size)

		val written = withContext(Dispatchers.IO) {
			transfer { Kernel32IO.WriteFile(handlePointer, buffer, total, null, overlappedPointer) }
		}
		if (written != total) throw IOException("Pipe write incomplete: $written of $total bytes")
	}

	override fun close() {
		buffer.close()
		transferred.close()
		k32.CloseHandle(event)
	}

	private suspend fun readInto(at: Int, len: Int): Boolean {
		var done = 0
		while (done < len) {
			val remaining = len - done
			val moved = transfer {
				Kernel32IO.ReadFile(handlePointer, buffer.share((at + done).toLong(), remaining.toLong()), remaining, null, overlappedPointer)
			}
			if (moved <= 0) return false
			done += moved
		}
		return true
	}

	private suspend inline fun transfer(issue: () -> Boolean): Int {
		if (!issue()) {
			// Read the code as the very next thing: k32.GetLastError() is itself a JNA call, and the
			// marshalling on the way into it can overwrite the value we wanted. Mistaking a pending
			// operation for a failed one frees buffer while the kernel is still reading out of it
			val err = Native.getLastError()
			if (err != WinError.ERROR_IO_PENDING) {
				// A broken pipe is just the client leaving, which the caller turns into a reconnect
				if (err != WinError.ERROR_BROKEN_PIPE) AppLogger.ipc.warn("Pipe operation failed: $err")
				return -1
			}
		}
		return awaitCompletion()
	}

	private suspend fun awaitCompletion(): Int {
		while (Kernel32IO.WaitForSingleObject(eventPointer, WAIT_SLICE_MILLIS) == WAIT_TIMEOUT) {
			if (currentCoroutineContext().isActive) continue

			Kernel32IO.CancelIoEx(handlePointer, overlappedPointer)
			Kernel32IO.GetOverlappedResult(handlePointer, overlappedPointer, transferred, true)
			currentCoroutineContext().ensureActive()
		}
		if (!Kernel32IO.GetOverlappedResult(handlePointer, overlappedPointer, transferred, true)) return -1
		return transferred.getInt(0)
	}
}
