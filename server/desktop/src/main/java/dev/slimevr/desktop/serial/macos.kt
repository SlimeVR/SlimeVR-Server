@file:Suppress("FunctionName", "ktlint:standard:function-naming")

package dev.slimevr.desktop.serial

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.ref.Reference

private const val SERIAL_SERVICE_CLASS = "IOSerialBSDClient"

// kIOFirstMatchNotification and kIOTerminatedNotification
private const val FIRST_MATCH_NOTIFICATION = "IOServiceFirstMatch"
private const val TERMINATED_NOTIFICATION = "IOServiceTerminate"

private const val MAIN_PORT_DEFAULT = 0
private const val KERN_SUCCESS = 0

internal fun interface ServiceCallback : Callback {
	fun invoke(refCon: Pointer?, iterator: Int)
}

internal interface IOKit : Library {
	fun IONotificationPortCreate(mainPort: Int): Pointer?
	fun IONotificationPortDestroy(port: Pointer)
	fun IONotificationPortSetDispatchQueue(port: Pointer, queue: Pointer)
	fun IOServiceMatching(name: String): Pointer?
	fun IOServiceAddMatchingNotification(
		port: Pointer,
		notificationType: String,
		matching: Pointer,
		callback: ServiceCallback,
		refCon: Pointer?,
		iterator: IntByReference,
	): Int
	fun IOIteratorNext(iterator: Int): Int
	fun IOObjectRelease(obj: Int): Int
}

internal interface LibSystem : Library {
	fun dispatch_queue_create(label: String, attr: Pointer?): Pointer?
	fun dispatch_release(queue: Pointer)
}

/** A change per serial service IOKit reports arriving or terminating. Callbacks run on a dispatch queue */
fun createMacOsPortChanges(): Flow<Unit> = callbackFlow {
	val ioKit = Native.load("IOKit", IOKit::class.java)
	val system = Native.load("c", LibSystem::class.java)

	fun drain(iterator: Int) {
		while (true) {
			val service = ioKit.IOIteratorNext(iterator)
			if (service == 0) return
			ioKit.IOObjectRelease(service)
		}
	}

	// Referenced until the flow closes so the native side keeps a live callback
	val callback = ServiceCallback { _, iterator ->
		drain(iterator)
		trySend(Unit)
	}

	val port = checkNotNull(ioKit.IONotificationPortCreate(MAIN_PORT_DEFAULT)) { "IONotificationPortCreate failed" }
	val queue = checkNotNull(system.dispatch_queue_create("dev.slimevr.serial", null)) { "dispatch_queue_create failed" }
	ioKit.IONotificationPortSetDispatchQueue(port, queue)

	// The call consumes the matching dictionary, so each subscription needs its own. The iterator is
	// drained once so the subscription is armed
	fun subscribe(notificationType: String): Int {
		val matching = checkNotNull(ioKit.IOServiceMatching(SERIAL_SERVICE_CLASS)) { "IOServiceMatching failed" }
		val iterator = IntByReference()
		val result = ioKit.IOServiceAddMatchingNotification(port, notificationType, matching, callback, null, iterator)
		check(result == KERN_SUCCESS) { "IOServiceAddMatchingNotification($notificationType) failed with $result" }
		drain(iterator.value)
		return iterator.value
	}
	val iterators = listOf(subscribe(FIRST_MATCH_NOTIFICATION), subscribe(TERMINATED_NOTIFICATION))

	awaitClose {
		iterators.forEach { ioKit.IOObjectRelease(it) }
		ioKit.IONotificationPortDestroy(port)
		system.dispatch_release(queue)
		Reference.reachabilityFence(callback)
	}
}
