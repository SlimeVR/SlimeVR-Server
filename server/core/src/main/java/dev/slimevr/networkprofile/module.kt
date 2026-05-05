package dev.slimevr.networkprofile

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope

data class NetworkInfo(
	val name: String?,
	val description: String?,
	val category: NetworkCategory?,
	val connectivity: Set<ConnectivityFlags>?,
	val connected: Boolean?,
)

enum class NetworkCategory(val value: Int) {
	PUBLIC(0), PRIVATE(1), DOMAIN_AUTHENTICATED(2);
	companion object { fun fromInt(value: Int) = entries.find { it.value == value } }
}

enum class ConnectivityFlags(val value: Int) {
	DISCONNECTED(0),
	IPV4_NOTRAFFIC(0x1), IPV6_NOTRAFFIC(0x2),
	IPV4_SUBNET(0x10), IPV4_LOCALNETWORK(0x20), IPV4_INTERNET(0x40),
	IPV6_SUBNET(0x100), IPV6_LOCALNETWORK(0x200), IPV6_INTERNET(0x400);
	companion object {
		fun fromInt(value: Int): Set<ConnectivityFlags> = if (value == 0) setOf(DISCONNECTED)
			else entries.filter { it != DISCONNECTED && (value and it.value) != 0 }.toSet()
	}
}

data class NetworkProfileState(
	val publicNetworks: List<NetworkInfo> = emptyList(),
	val isSupported: Boolean = false,
)

sealed interface NetworkProfileActions {
	data class UpdateNetworks(val networks: List<NetworkInfo>) : NetworkProfileActions
}

typealias NetworkProfileContext = Context<NetworkProfileState, NetworkProfileActions>
typealias NetworkProfileBehaviourType = Behaviour<NetworkProfileState, NetworkProfileActions, NetworkProfileManager>

object DefaultNetworkProfileBehaviour : NetworkProfileBehaviourType {
	override fun reduce(state: NetworkProfileState, action: NetworkProfileActions) = when (action) {
		is NetworkProfileActions.UpdateNetworks -> state.copy(publicNetworks = action.networks)
	}
}

class NetworkProfileManager(val context: NetworkProfileContext) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(scope: CoroutineScope, isSupported: Boolean): NetworkProfileManager {
			val context = Context.create(
				initialState = NetworkProfileState(isSupported = isSupported),
				scope = scope,
				behaviours = listOf(DefaultNetworkProfileBehaviour),
				name = "NetworkProfile",
			)
			return NetworkProfileManager(context)
		}
	}
}
