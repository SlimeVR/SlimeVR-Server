package dev.slimevr.desktop.timing

import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.PreciseWaiter
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.util.concurrent.locks.LockSupport
import kotlin.time.Duration

/** The [PreciseWaiter] this platform can hold a timed loop to */
suspend fun createDesktopWaiter(): PreciseWaiter = when (CURRENT_PLATFORM) {
	// A Windows park resolves to the scheduler tick, 1ms at best and 15.6ms by default, so a 2ms
	// deadline wakes two ticks late and a 500Hz loop settles near 330Hz
	Platform.WINDOWS -> createWindowsWaiter()

	Platform.LINUX, Platform.OSX, Platform.UNKNOWN -> ParkWaiter
}

private suspend fun createWindowsWaiter(): PreciseWaiter = runCatching { WindowsWaitableTimer.create() }
	.onSuccess { AppLogger.skeleton.info("Timed loops waiting on the Windows high-resolution waitable timer") }
	.getOrElse {
		AppLogger.skeleton.warn(it, "No high-resolution timer, timed loops fall back to park granularity")
		ParkWaiter
	}

/** The JVM park, for platforms whose timer needs no help and for when the binding won't load */
private object ParkWaiter : PreciseWaiter {
	override fun sleep(duration: Duration) = LockSupport.parkNanos(duration.inWholeNanoseconds)
}

private const val CREATE_WAITABLE_TIMER_MANUAL_RESET = 0x1
private const val CREATE_WAITABLE_TIMER_HIGH_RESOLUTION = 0x2
private const val TIMER_ALL_ACCESS = 0x1F0003
private const val INFINITE = -1
private const val NANOS_PER_TICK = 100

/**
 * One waitable timer, bound to the thread that created it.
 */
private class WindowsWaitableTimer private constructor(
	private val handle: MemorySegment,
	private val dueTime: MemorySegment,
) : PreciseWaiter {
	override fun sleep(duration: Duration) {
		dueTime.set(JAVA_LONG, 0, -(duration.inWholeNanoseconds / NANOS_PER_TICK))
		val armed = setWaitableTimer.invokeExact(
			handle,
			dueTime,
			0,
			MemorySegment.NULL,
			MemorySegment.NULL,
			0,
		) as Int
		if (armed == 0) return
		waitForSingleObject.invokeExact(handle, INFINITE) as Int
	}

	companion object {
		private val linker = Linker.nativeLinker()
		private val kernel32 = SymbolLookup.libraryLookup("kernel32.dll", Arena.global())

		private val createWaitableTimerEx = linker.downcallHandle(
			kernel32.findOrThrow("CreateWaitableTimerExW"),
			FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_INT, JAVA_INT),
		)

		private val setWaitableTimer = linker.downcallHandle(
			kernel32.findOrThrow("SetWaitableTimer"),
			FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, JAVA_INT, ADDRESS, ADDRESS, JAVA_INT),
		)

		private val waitForSingleObject = linker.downcallHandle(
			kernel32.findOrThrow("WaitForSingleObject"),
			FunctionDescriptor.of(JAVA_INT, ADDRESS, JAVA_INT),
		)

		/** Throws when the binding won't load or the OS predates the high-resolution flag */
		fun create(): WindowsWaitableTimer {
			val handle = createWaitableTimerEx.invokeExact(
				MemorySegment.NULL,
				MemorySegment.NULL,
				CREATE_WAITABLE_TIMER_MANUAL_RESET or CREATE_WAITABLE_TIMER_HIGH_RESOLUTION,
				TIMER_ALL_ACCESS,
			) as MemorySegment
			check(handle.address() != 0L) { "CreateWaitableTimerExW returned NULL" }

			return WindowsWaitableTimer(handle, Arena.ofAuto().allocate(JAVA_LONG))
		}
	}
}
