package dev.slimevr.udp

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest
import solarxr_protocol.datatypes.hardware_info.BoardType
import solarxr_protocol.datatypes.hardware_info.McuType

// Firmware re-handshakes about once a second while it looks for a server
internal const val UNKNOWN_DEVICE_TIMEOUT_MS = 5000L

/** A device handshaking without being on the allowed list. */
data class UnknownDeviceState(
	val hardwareId: String,
	// Same "ip/port" form as UDPConnectionState.address
	val address: String,
	val boardType: BoardType,
	val mcuType: McuType,
	val firmwareVersion: String?,
	val lastSeen: Long,
)

class UnknownDeviceExpiryBehaviour : UdpServerBehaviour {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: UdpServer) {
		receiver.context.state
			.map { state -> state.unknownDevices.values.minOfOrNull { it.lastSeen } }
			.distinctUntilChanged()
			.transformLatest { oldest ->
				if (oldest == null) return@transformLatest
				delay(oldest + UNKNOWN_DEVICE_TIMEOUT_MS - receiver.clock())
				emit(oldest)
			}
			.onEach { oldest -> receiver.context.dispatch(UdpServerActions.UnknownDevicesExpired(cutoff = oldest)) }
			.launchIn(receiver.context.scope)
	}
}
