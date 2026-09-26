package dev.slimevr.android.timing

import dev.slimevr.util.PreciseWaiter
import java.util.concurrent.locks.LockSupport
import kotlin.time.Duration

fun createAndroidWaiter(): PreciseWaiter = ParkWaiter

private object ParkWaiter : PreciseWaiter {
	override fun sleep(duration: Duration) = LockSupport.parkNanos(duration.inWholeNanoseconds)
}
