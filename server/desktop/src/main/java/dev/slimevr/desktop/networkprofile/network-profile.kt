package dev.slimevr.desktop.networkprofile

import com.sun.jna.Callback
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.COM.Dispatch
import com.sun.jna.platform.win32.Guid.CLSID
import com.sun.jna.platform.win32.Guid.IID
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOLByReference
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.OleAuto
import com.sun.jna.platform.win32.WTypes
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.networkprofile.ConnectivityFlags
import dev.slimevr.networkprofile.NetworkCategory
import dev.slimevr.networkprofile.NetworkInfo
import dev.slimevr.networkprofile.NetworkProfileActions
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Suppress("ktlint:standard:function-naming")
interface Iphlpapi : Library {
	fun NotifyIpInterfaceChange(
		family: Int,
		callback: Callback,
		context: Pointer?,
		initialNotification: Int,
		notificationHandle: PointerByReference,
	): Int

	fun CancelMibChangeNotify2(handle: Pointer): Int

	companion object {
		val INSTANCE: Iphlpapi = Native.load("Iphlpapi", Iphlpapi::class.java)
	}
}

@Suppress("ktlint:standard:backing-property-naming")
private class INetworkConnection(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	fun getNetwork(): INetwork {
		val pNetwork = PointerByReference()
		val hr = _invokeNativeInt(7, arrayOf(pointer, pNetwork))
		COMUtils.checkRC(HRESULT(hr))
		return INetwork(pNetwork.value)
	}
}

@Suppress("ktlint:standard:backing-property-naming")
private class IEnumNetworkConnections(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	fun next(): INetworkConnection? {
		val ppNetworkConnections = PointerByReference()
		val nFetched = IntByReference()
		val hr = _invokeNativeInt(8, arrayOf(pointer, 1, ppNetworkConnections, nFetched))
		COMUtils.checkRC(HRESULT(hr))
		return if (nFetched.value == 0) null else INetworkConnection(ppNetworkConnections.value)
	}
}

@Suppress("ktlint:standard:backing-property-naming")
private class INetworkListManager(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	fun getNetworkConnections(): IEnumNetworkConnections {
		val pEnumNetworkConnections = PointerByReference()
		val hr = _invokeNativeInt(9, arrayOf(pointer, pEnumNetworkConnections))
		COMUtils.checkRC(HRESULT(hr))
		return IEnumNetworkConnections(pEnumNetworkConnections.value)
	}
}

@Suppress("ktlint:standard:backing-property-naming")
private class INetwork(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}

	fun getName(): String? {
		val pStr = PointerByReference()
		val hr = _invokeNativeInt(7, arrayOf(pointer, pStr))
		COMUtils.checkRC(HRESULT(hr))
		val bstr = WTypes.BSTR(pStr.value)
		val stringValue = bstr.value
		OleAuto.INSTANCE.SysFreeString(bstr)
		return stringValue
	}

	fun getDescription(): String? {
		val pStr = PointerByReference()
		val hr = _invokeNativeInt(9, arrayOf(pointer, pStr))
		COMUtils.checkRC(HRESULT(hr))
		val bstr = WTypes.BSTR(pStr.value)
		val stringValue = bstr.value
		OleAuto.INSTANCE.SysFreeString(bstr)
		return stringValue
	}

	fun isConnected(): Boolean {
		val bool = VARIANT_BOOLByReference()
		val hr = _invokeNativeInt(16, arrayOf(pointer, bool))
		COMUtils.checkRC(HRESULT(hr))
		return bool.value.booleanValue()
	}

	fun getConnectivity(): Set<ConnectivityFlags> {
		val connectivity = IntByReference()
		val hr = _invokeNativeInt(17, arrayOf(pointer, connectivity))
		COMUtils.checkRC(HRESULT(hr))
		return ConnectivityFlags.fromInt(connectivity.value)
	}

	fun getCategory(): NetworkCategory? {
		val category = IntByReference()
		val hr = _invokeNativeInt(18, arrayOf(pointer, category))
		COMUtils.checkRC(HRESULT(hr))
		return NetworkCategory.fromInt(category.value)
	}
}

private fun enumerateNetworks(): List<NetworkInfo> = try {
	Ole32.INSTANCE.CoInitializeEx(null, 0)
	val clsid = CLSID("dcb00c01-570f-4a9b-8d69-199fdba5723b")
	val iid = IID("dcb00000-570f-4a9b-8d69-199fdba5723b")
	val ptr = PointerByReference()
	COMUtils.checkRC(Ole32.INSTANCE.CoCreateInstance(clsid, null, WTypes.CLSCTX_ALL, iid, ptr))
	val mgr = INetworkListManager(ptr.value)
	val result = mgr.getNetworkConnections().use { conns ->
		generateSequence { conns.next() }
			.map { conn ->
				conn.use {
					conn.getNetwork().use { network ->
						NetworkInfo(
							network.getName(),
							network.getDescription(),
							network.getCategory(),
							network.getConnectivity(),
							network.isConnected(),
						)
					}
				}
			}.toList()
	}
	mgr.close()
	Ole32.INSTANCE.CoUninitialize()
	result
} catch (e: Exception) {
	println(e.stackTraceToString())
	emptyList()
}

suspend fun setupDesktopNetworkProfileChecker(scope: CoroutineScope, manager: NetworkProfileManager) {
	if (CURRENT_PLATFORM != Platform.WINDOWS) return

	val notificationHandle = PointerByReference()
	val callback = object : Callback {
		@Suppress("UNUSED")
		fun callback(context: Pointer, row: Pointer, notificationType: Int) {
			scope.safeLaunch {
				val networks = enumerateNetworks()
					.filter { it.connected == true && it.category == NetworkCategory.PUBLIC }
				manager.context.dispatch(NetworkProfileActions.UpdateNetworks(networks))
			}
		}
	}

	val result = Iphlpapi.INSTANCE.NotifyIpInterfaceChange(
		0,
		callback,
		null,
		1,
		notificationHandle,
	)

	if (result == 0) {
		val initialNetworks = enumerateNetworks()
			.filter { it.connected == true && it.category == NetworkCategory.PUBLIC }
		manager.context.dispatch(NetworkProfileActions.UpdateNetworks(initialNetworks))
		try {
			delay(Long.MAX_VALUE.milliseconds)
		} catch (e: Exception) {
			val handle = notificationHandle.value
			if (handle != null) {
				try {
					Iphlpapi.INSTANCE.CancelMibChangeNotify2(handle)
				} catch (ignore: Exception) {}
			}
		}
	}
}
