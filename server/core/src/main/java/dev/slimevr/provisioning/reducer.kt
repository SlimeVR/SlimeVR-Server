package dev.slimevr.provisioning

import solarxr_protocol.rpc.TrackerProvisioningStatus
import solarxr_protocol.rpc.WifiScanStatus

private fun ProvisioningManagerState.updateTracker(
	portLocation: String,
	transform: (TrackerProvisioningState) -> TrackerProvisioningState,
) = copy(
	trackers = trackers +
		(
			portLocation to transform(
				trackers[portLocation] ?: TrackerProvisioningState(portLocation, TrackerProvisioningStatus.SERIAL_INIT),
			)
			),
)

fun reduce(state: ProvisioningManagerState, action: ProvisioningActions): ProvisioningManagerState = when (action) {
	is ProvisioningActions.ScanPortSelected -> state.copy(
		scan = state.scan.copy(portLocation = action.portLocation),
	)

	is ProvisioningActions.ScanStatusChanged -> state.copy(
		scan = state.scan.copy(status = action.status),
	)

	is ProvisioningActions.ScanResults -> state.copy(
		scan = state.scan.copy(networks = action.networks),
	)

	is ProvisioningActions.ScanClear -> state.copy(scan = ScanState(status = WifiScanStatus.NONE))

	is ProvisioningActions.TrackerStatusChanged -> state.updateTracker(action.portLocation) {
		it.copy(status = action.status)
	}

	is ProvisioningActions.TrackerMacAddressObtained -> state.updateTracker(action.portLocation) {
		it.copy(macAddress = action.mac)
	}

	is ProvisioningActions.TrackerRemoved -> state.copy(trackers = state.trackers - action.portLocation)

	is ProvisioningActions.TrackersClear -> state.copy(trackers = emptyMap())
}
