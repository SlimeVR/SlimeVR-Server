package dev.slimevr.desktop.vrchat

import com.sun.jna.Callback
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.desktop.networkprofile.Iphlpapi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.lang.ref.Reference
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

private val RESOLVE_INTERVAL = 5.seconds

fun resolveDesktopLocalIpAddress(): String = try {
	val candidates = Collections.list(NetworkInterface.getNetworkInterfaces())
		.asSequence()
		.filter { iface -> iface.isUp && !iface.isLoopback && !iface.isVirtual }
		.flatMap { iface -> Collections.list(iface.inetAddresses).asSequence() }
		.filter { address -> !address.isLoopbackAddress && !address.hostAddress.contains(':') }
		.toList()

	val siteLocal = candidates.firstOrNull { address -> address.isSiteLocalAddress }
	(siteLocal ?: candidates.firstOrNull())?.hostAddress
		?: InetAddress.getLoopbackAddress().hostAddress
} catch (_: Exception) {
	InetAddress.getLoopbackAddress().hostAddress
}

/** [resolveDesktopLocalIpAddress], resolved again whenever the machine's addresses may have changed. */
fun observeDesktopLocalIpAddress(scope: CoroutineScope): StateFlow<String> = addressChanges()
	.map { resolveDesktopLocalIpAddress() }
	.flowOn(Dispatchers.IO)
	.stateIn(scope, SharingStarted.Eagerly, resolveDesktopLocalIpAddress())

// The JVM has no address change notification, so outside Windows we resolve again on a timer
private fun addressChanges(): Flow<Unit> = if (CURRENT_PLATFORM == Platform.WINDOWS) {
	windowsAddressChanges()
} else {
	flow {
		while (true) {
			delay(RESOLVE_INTERVAL)
			emit(Unit)
		}
	}
}

private class AddressChangeCallback(private val onChange: () -> Unit) : Callback {
	@Suppress("UNUSED")
	fun callback(context: Pointer?, row: Pointer?, notificationType: Int) = onChange()
}

private fun windowsAddressChanges(): Flow<Unit> = callbackFlow {
	val callback = AddressChangeCallback { trySend(Unit) }
	val handle = PointerByReference()
	Iphlpapi.INSTANCE.NotifyUnicastIpAddressChange(0, callback, null, 0, handle)
	awaitClose {
		handle.value?.let { Iphlpapi.INSTANCE.CancelMibChangeNotify2(it) }
		Reference.reachabilityFence(callback)
	}
}.conflate()
