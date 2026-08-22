package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.device.Device
import dev.slimevr.firmware.isOnlineStatus
import dev.slimevr.provisioning.ProvisioningManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.StartWifiProvisioningRequest
import solarxr_protocol.rpc.StartWifiScanRequest
import solarxr_protocol.rpc.StopWifiProvisioningRequest
import solarxr_protocol.rpc.StopWifiScanRequest
import solarxr_protocol.rpc.TrackerProvisioningState
import solarxr_protocol.rpc.TrackerProvisioningStatus
import solarxr_protocol.rpc.WifiProvisioningStatusResponse
import solarxr_protocol.rpc.WifiScanStatusResponse

class ProvisioningBehaviour(
	private val server: VRServer,
	private val provisioningManager: ProvisioningManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<StartWifiProvisioningRequest> { event ->
			val ssid = event.ssid ?: return@on

			provisioningManager.startProvisioning(
				server,
				ssid,
				event.password,
			)
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StopWifiProvisioningRequest> {
			provisioningManager.stopProvisioning()
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StartWifiScanRequest> {
			provisioningManager.startWifiScan()
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<StopWifiScanRequest> {
			provisioningManager.stopWifiScan()
		}.launchIn(receiver.context.scope)

		provisioningManager.context.state.map { it.scan }.distinctUntilChanged().drop(1).onEach { scan ->
			receiver.sendRpc(
				WifiScanStatusResponse(
					status = scan.status,
					networks = scan.networks,
				),
			)
		}.launchIn(receiver.context.scope)

		combine(
			provisioningManager.context.state.map { it.trackers },
			server.context.state.map { it.devices },
		) { provisioningTrackers, devices ->
			buildUnifiedTrackerList(provisioningTrackers, devices)
		}
			.distinctUntilChanged()
			.drop(1)
			.onEach { trackers ->
				receiver.sendRpc(WifiProvisioningStatusResponse(trackers = trackers))
			}
			.launchIn(receiver.context.scope)
	}
}

private fun buildUnifiedTrackerList(
	provisioningTrackers: Map<String, dev.slimevr.provisioning.TrackerProvisioningState>,
	devices: Map<Int, Device>,
): List<TrackerProvisioningState> {
	val onlineMacs = devices.values.mapNotNull { device ->
		val state = device.context.state.value
		val mac = state.macAddress?.uppercase() ?: return@mapNotNull null
		if (!isOnlineStatus(state.status)) return@mapNotNull null
		mac
	}.toSet()

	val provisioningEntries = provisioningTrackers.values.mapNotNull {
		if (it.status == TrackerProvisioningStatus.DONE && it.macAddress?.uppercase() !in onlineMacs) {
			return@mapNotNull null
		}
		TrackerProvisioningState(
			port = it.portLocation,
			macAddress = it.macAddress,
			status = it.status,
		)
	}

	val provisioningMacs = provisioningTrackers.values
		.mapNotNull { it.macAddress?.uppercase() }
		.toSet()

	val connectedEntries = devices.values.mapNotNull { device ->
		val state = device.context.state.value
		val mac = state.macAddress?.uppercase() ?: return@mapNotNull null
		if (mac in provisioningMacs) return@mapNotNull null
		if (mac !in onlineMacs) return@mapNotNull null

		TrackerProvisioningState(
			port = null,
			macAddress = mac,
			status = TrackerProvisioningStatus.DONE,
		)
	}

	return provisioningEntries + connectedEntries
}
