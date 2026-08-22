package dev.slimevr.device

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeviceStatsBehaviour : DeviceBehaviour {
	override fun reduce(state: DeviceState, action: DeviceActions) = when (action) {
		is DeviceActions.Update -> action.transform(state)
		is DeviceActions.PacketStats -> state.copy(packetsReceived = action.packetsReceived, packetsLost = action.packetsLost)
	}

	override fun observe(receiver: Device) {
		receiver.context.state.onEach {
// 			AppLogger.device.info("Device state changed", it)
		}.launchIn(receiver.context.scope)
	}
}
