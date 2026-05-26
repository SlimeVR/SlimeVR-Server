package dev.slimevr.desktop

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMException
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.COM.Dispatch
import com.sun.jna.platform.win32.Guid.CLSID
import com.sun.jna.platform.win32.Guid.IID
import com.sun.jna.platform.win32.IPHlpAPI
import com.sun.jna.platform.win32.IPHlpAPI.AF_UNSPEC
import com.sun.jna.platform.win32.OaIdl.VARIANT_BOOLByReference
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.OleAuto
import com.sun.jna.platform.win32.WTypes
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinError
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary
import dev.slimevr.ConnectivityFlags
import dev.slimevr.NetworkCategory
import dev.slimevr.NetworkInfo
import dev.slimevr.NetworkProfileChecker
import io.eiren.util.OperatingSystem
import io.eiren.util.logging.LogManager

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/nn-netlistmgr-inetworkconnection">INetworkConnection interface (netlistmgr.h)</a>
 */
@Suppress("ktlint:standard:backing-property-naming", "ktlint:standard:function-naming")
class INetworkConnection(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	companion object VTable {
		// IUnknown +3
		// IDispatch +4
		// IEnumNetworkConnections
		const val GetNetwork = 7
		const val IsConnectedToInternet = 8
		const val IsConnected = 9
		const val GetConnectivity = 10
		const val GetConnectionId = 11
		const val GetAdapterId = 12
		const val GetDomainType = 13
	}

	@Throws(COMException::class)
	fun GetNetwork(): INetwork {
		val pNetwork = PointerByReference()
		val hr = _invokeNativeInt(VTable.GetNetwork, arrayOf(pointer, pNetwork))
		COMUtils.checkRC(HRESULT(hr))
		return INetwork(pNetwork.value)
	}
}

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/nn-netlistmgr-ienumnetworkconnections">IEnumNetworkConnections interface (netlistmgr.h)</a>
 */
@Suppress("ktlint:standard:backing-property-naming", "ktlint:standard:function-naming")
class IEnumNetworkConnections(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	companion object VTable {
		// IUnknown +3
		// IDispatch +4
		// IEnumNetworkConnections
		const val _NewEnum = 7
		const val Next = 8
		const val Skip = 9
		const val Reset = 10
		const val Clone = 11
	}

	@Throws(COMException::class)
	fun Next(): INetworkConnection? {
		val ppNetworkConnections = PointerByReference()
		val nFetched = IntByReference()
		val hr = _invokeNativeInt(VTable.Next, arrayOf(pointer, 1, ppNetworkConnections, nFetched))
		COMUtils.checkRC(HRESULT(hr))
		if (nFetched.value.equals(0)) return null
		return INetworkConnection(ppNetworkConnections.value)
	}
}

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/nn-netlistmgr-inetworklistmanager">INetworkListManager interface (netlistmgr.h)</a>
 */
@Suppress("ktlint:standard:backing-property-naming", "ktlint:standard:function-naming")
class INetworkListManager(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	companion object VTable {
		// IUnknown +3
		// IDispatch +4
		// INetworkListManager
		const val GetNetworks = 7
		const val GetNetwork = 8
		const val GetNetworkConnections = 9
		const val GetNetworkConnection = 10
		const val IsConnectedToInternet = 11
		const val IsConnected = 12
		const val GetConnectivity = 13
	}

	@Throws(COMException::class)
	fun GetNetworkConnections(): IEnumNetworkConnections {
		val pEnumNetworkConnections = PointerByReference()
		val hr = _invokeNativeInt(VTable.GetNetworkConnections, arrayOf(pointer, pEnumNetworkConnections))
		COMUtils.checkRC(HRESULT(hr))
		return IEnumNetworkConnections(pEnumNetworkConnections.value)
	}
}

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/nn-netlistmgr-inetwork">INetwork interface (netlistmgr.h)</a>
 */
@Suppress("ktlint:standard:backing-property-naming", "ktlint:standard:function-naming")
class INetwork(instance: Pointer?) :
	Dispatch(instance),
	AutoCloseable {
	override fun close() {
		Release()
	}
	companion object VTable {
		// IUnknown +3
		// IDispatch +4
		// INetworkListManager
		const val GetName = 7
		const val SetName = 8
		const val GetDescription = 9
		const val SetDescription = 10
		const val GetNetworkId = 11
		const val GetDomainType = 12
		const val GetNetworkConnections = 13
		const val GetTimeCreatedAndConnected = 14
		const val IsConnectedToInternet = 15
		const val IsConnected = 16
		const val GetConnectivity = 17
		const val GetCategory = 18
		const val SetCategory = 19
	}

	@Throws(COMException::class)
	private fun getNativeString(vtableId: Int): String? {
		val pStr = PointerByReference()
		val hr = _invokeNativeInt(vtableId, arrayOf(pointer, pStr))
		COMUtils.checkRC(HRESULT(hr))
		val bstr = WTypes.BSTR(pStr.value)
		val stringValue = bstr.value
		OleAuto.INSTANCE.SysFreeString(bstr)
		return stringValue
	}

	@Throws(COMException::class)
	fun GetName(): String? = getNativeString(VTable.GetName)

	@Throws(COMException::class)
	fun GetDescription(): String? = getNativeString(VTable.GetDescription)

	@Throws(COMException::class)
	fun IsConnected(): Boolean {
		val bool = VARIANT_BOOLByReference()
		val hr = _invokeNativeInt(VTable.IsConnected, arrayOf(pointer, bool))
		COMUtils.checkRC(HRESULT(hr))
		return bool.value.booleanValue()
	}

	@Throws(COMException::class)
	fun GetConnectivity(): Set<ConnectivityFlags> {
		val connectivity = IntByReference()
		val hr = _invokeNativeInt(VTable.GetConnectivity, arrayOf(pointer, connectivity))
		COMUtils.checkRC(HRESULT(hr))
		return ConnectivityFlags.fromInt(connectivity.value)
	}

	@Throws(COMException::class)
	fun GetCategory(): NetworkCategory? {
		val category = IntByReference()
		val hr = _invokeNativeInt(VTable.GetCategory, arrayOf(pointer, category))
		COMUtils.checkRC(HRESULT(hr))
		return NetworkCategory.fromInt(category.value)
	}
}

/**
 * Network List Manager API wrapper
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/nla/about-the-network-list-manager-api">Network List Manager API</a>
 * @throws COMException
 */
class COMNetworkManager
@Throws(COMException::class)
constructor() : AutoCloseable {
	var instance: INetworkListManager
	private var shouldUninitialize = false

	init {
		shouldUninitialize = COMUtils.SUCCEEDED(Ole32.INSTANCE.CoInitialize(null))

		val CLSID_NetworkListManager = CLSID("dcb00c01-570f-4a9b-8d69-199fdba5723b")
		val IID_INetworkListManager = IID("dcb00000-570f-4a9b-8d69-199fdba5723b")

		val ptr = PointerByReference()
		val hr = Ole32.INSTANCE.CoCreateInstance(
			CLSID_NetworkListManager,
			null,
			WTypes.CLSCTX_ALL,
			IID_INetworkListManager,
			ptr,
		)
		try {
			COMUtils.checkRC(hr)
		} catch (err: Exception) {
			if (shouldUninitialize) {
				Ole32.INSTANCE.CoUninitialize()
			}
			throw err
		}

		instance = INetworkListManager(ptr.value)
	}

	override fun close() {
		instance.close()
		if (shouldUninitialize) {
			Ole32.INSTANCE.CoUninitialize()
		}
	}
}

@Suppress("ktlint:standard:function-naming", "ktlint:standard:class-naming")
interface NetIOAPI : IPHlpAPI {
	enum class MIB_NOTIFICATION_TYPE(val native: Int) {
		MibParameterNotification(0),
		MibAddInstance(1),
		MibDeleteInstance(2),
		MibInitialNotification(3),
		;

		companion object {
			fun fromNative(native: Int) = byNative[native]
			private val byNative = entries.associateBy { it.native }
		}
	}
	interface IPINTERFACE_CHANGE_CALLBACK : StdCallLibrary.StdCallCallback {
		fun callback(
			context: Pointer?,
			row: Pointer?,
			/* MIB_NOTIFICATION_TYPE */
			notificationType: Int,
		)
	}

	fun NotifyIpInterfaceChange(family: Int, callback: IPINTERFACE_CHANGE_CALLBACK, context: Pointer?, initialNotification: WinDef.BOOL, notificationHandle: WinNT.HANDLEByReference): Long
	fun CancelMibChangeNotify2(notificationHandle: WinNT.HANDLE): Long

	companion object {
		val INSTANCE: NetIOAPI = Native.load("Iphlpapi", NetIOAPI::class.java)
	}
}

fun enumerateNetworks(): List<NetworkInfo>? {
	if (OperatingSystem.currentPlatform != OperatingSystem.WINDOWS) {
		return null
	}
	try {
		COMNetworkManager().use { netMgr ->
			netMgr.instance.GetNetworkConnections().use { conns ->
				return generateSequence { conns.Next() }
					.map { conn ->
						conn.use {
							conn.GetNetwork().use { network ->
								NetworkInfo(
									network.GetName(),
									network.GetDescription(),
									network.GetCategory(),
									network.GetConnectivity(),
									network.IsConnected(),
								)
							}
						}
					}.toList()
			}
		}
	} catch (e: Exception) {
		LogManager.severe("[DesktopNetworkProfileChecker] Exception when enumerating networks", e)
	}
	return null
}

class DesktopNetworkProfileChecker :
	NetworkProfileChecker(),
	Runnable {
	private val thread = Thread(this, "DesktopNetworkProfileChecker runner")
	private var publicNetworksLocal: List<NetworkInfo> = listOf()

	override val isSupported: Boolean
		get() = OperatingSystem.currentPlatform == OperatingSystem.WINDOWS

	override val publicNetworks: List<NetworkInfo>
		get() = publicNetworksLocal

	init {
		if (isSupported) {
			thread.start()
		}
	}

	override fun run() {
		val handle = WinNT.HANDLEByReference()
		val callback = object : NetIOAPI.IPINTERFACE_CHANGE_CALLBACK {
			override fun callback(
				context: Pointer?,
				row: Pointer?,
				notificationType: Int,
			) {
				synchronized(publicNetworksLocal) {
					publicNetworksLocal = enumerateNetworks()?.filter { net ->
						net.connected && net.category == NetworkCategory.PUBLIC
					} ?: listOf()
				}
			}
		}

		val ret = NetIOAPI.INSTANCE.NotifyIpInterfaceChange(AF_UNSPEC, callback, null, WinDef.BOOL(true), handle)
		if (ret != WinError.ERROR_SUCCESS.toLong()) {
			LogManager.warning("[DesktopNetworkProfileChecker] Error registering network interface change callback, public interface detection will not work: $ret")
			return
		}

		try {
			while (true) Thread.sleep(Long.MAX_VALUE)
		} catch (_: InterruptedException) {} finally {
			handle.value?.let {
				try {
					NetIOAPI.INSTANCE.CancelMibChangeNotify2(it)
				} catch (_: Exception) {}
			}
		}
	}
}
