package dev.slimevr

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
		fun fromInt(value: Int): Set<ConnectivityFlags> = if (value == 0) {
			setOf(DISCONNECTED)
		} else {
			values().filter { it != DISCONNECTED && (value and it.value) != 0 }.toSet()
		}
	}
}

abstract class NetworkProfileChecker {
	abstract val isSupported: Boolean
	abstract val publicNetworks: List<NetworkInfo>
}

class NetworkProfileCheckerStub : NetworkProfileChecker() {
	override val isSupported: Boolean
		get() = false
	override val publicNetworks: List<NetworkInfo>
		get() = listOf()
}
