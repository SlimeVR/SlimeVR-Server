package dev.slimevr.desktop

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMException
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
import dev.slimevr.VRServer
import io.eiren.util.OperatingSystem
import solarxr_protocol.rpc.StatusData
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusPublicNetworkT
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

data class NetworkInfo(
	val name: String?,
	val description: String?,
	val category: NetworkCategory?,
	val connectivity: Set<ConnectivityFlags>?,
	val connected: Boolean?,
)

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/ne-netlistmgr-nlm_network_category">NLM_NETWORK_CATEGORY enumeration (netlistmgr.h)</a>
 */
enum class NetworkCategory(val value: Int) {
	PUBLIC(0),
	PRIVATE(1),
	DOMAIN_AUTHENTICATED(2),
	;

	companion object {
		fun fromInt(value: Int) = values().find { it.value == value }
	}
}

/**
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/netlistmgr/ne-netlistmgr-nlm_connectivity">NLM_CONNECTIVITY enumeration (netlistmgr.h)</a>
 */
enum class ConnectivityFlags(val value: Int) {
	DISCONNECTED(0),
	IPV4_NOTRAFFIC(0x1),
	IPV6_NOTRAFFIC(0x2),
	IPV4_SUBNET(0x10),
	IPV4_LOCALNETWORK(0x20),
	IPV4_INTERNET(0x40),
	IPV6_SUBNET(0x100),
	IPV6_LOCALNETWORK(0x200),
	IPV6_INTERNET(0x400),
	;

	companion object {
		fun fromInt(value: Int): Set<ConnectivityFlags> =
			if (value == 0) {
				setOf(DISCONNECTED)
			} else {
				values().filter { it != DISCONNECTED && (value and it.value) != 0 }.toSet()
			}
	}
}

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

fun enumerateNetworks(): List<NetworkInfo>? {
	if (OperatingSystem.currentPlatform != OperatingSystem.WINDOWS) {
		return null
	}
	try {
		COMNetworkManager().use { netmgr ->
			netmgr.instance.GetNetworkConnections().use { conns ->
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
	} catch (err: Exception) {
		println(err.stackTraceToString())
	}
	return null
}

class NetworkProfileChecker(private val server: VRServer) {
	private val updateTickTimer = Timer("NetworkProfileCheck")
	private var lastPublicNetworkStatus: UInt = 0u
	private var numPublicNetworks = 0

	init {
		if (OperatingSystem.currentPlatform == OperatingSystem.WINDOWS) {
			this.updateTickTimer.scheduleAtFixedRate(0, 3000) {
				val currentNumPublicNetworks = enumerateNetworks()?.filter { net ->
					net.connected == true && net.category == NetworkCategory.PUBLIC
				} ?: listOf()
				val currentNumPublicNetworksCount = currentNumPublicNetworks.count()

				if (numPublicNetworks != currentNumPublicNetworksCount) {
					numPublicNetworks = currentNumPublicNetworksCount
					if (lastPublicNetworkStatus != 0u) {
						server.statusSystem.removeStatus(lastPublicNetworkStatus)
						lastPublicNetworkStatus = 0u
					}

					if (lastPublicNetworkStatus == 0u && numPublicNetworks > 0) {
						lastPublicNetworkStatus = server.statusSystem.addStatus(
							StatusDataUnion().apply {
								type = StatusData.StatusPublicNetwork
								value = StatusPublicNetworkT().apply {
									adapters = currentNumPublicNetworks.map { it.name }.toTypedArray()
								}
							},
							false,
						)
					}
				}
			}
		}
	}
}
