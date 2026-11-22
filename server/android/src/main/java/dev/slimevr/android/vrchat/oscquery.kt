package dev.slimevr.android.vrchat

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

fun resolveAndroidLocalIpAddress(): String = try {
	val candidates = Collections.list(NetworkInterface.getNetworkInterfaces())
		.asSequence()
		.filter { iface -> iface.isUp && !iface.isLoopback && !iface.isVirtual }
		.flatMap { iface -> Collections.list(iface.inetAddresses).asSequence() }
		.filter { address -> !address.isLoopbackAddress && !address.hostAddress!!.contains(':') }
		.toList()

	val siteLocal = candidates.firstOrNull { address -> address.isSiteLocalAddress }
	(siteLocal ?: candidates.firstOrNull())?.hostAddress
		?: InetAddress.getLoopbackAddress().hostAddress
} catch (_: Exception) {
	InetAddress.getLoopbackAddress().hostAddress
}

/** [resolveAndroidLocalIpAddress], resolved again whenever the default network or its addresses change. */
fun observeAndroidLocalIpAddress(context: Context, scope: CoroutineScope): StateFlow<String> = callbackFlow {
	val connectivity = context.getSystemService(ConnectivityManager::class.java)
	val callback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			trySend(Unit)
		}

		override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
			trySend(Unit)
		}

		override fun onLost(network: Network) {
			trySend(Unit)
		}
	}
	connectivity.registerDefaultNetworkCallback(callback)
	awaitClose { connectivity.unregisterNetworkCallback(callback) }
}
	.conflate()
	.map { resolveAndroidLocalIpAddress() }
	.flowOn(Dispatchers.IO)
	.stateIn(scope, SharingStarted.Eagerly, resolveAndroidLocalIpAddress())
