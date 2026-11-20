package dev.slimevr.hid.behaviours.v3

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDDeviceStateV3
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HID_TIME_UNKNOWN
import dev.slimevr.tracker.TrackerActions

class HIDDeviceStateBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceStateV3> { packet ->
			receiver.getDevice(packet.hidId)?.context?.dispatch(
				DeviceActions.Update {
					copy(
						batteryLevel = packet.batteryLevel,
						batteryVoltage = packet.batteryVoltage,
						batteryRemainingRuntime = if (packet.runtimeSeconds == HID_TIME_UNKNOWN) {
							batteryRemainingRuntime
						} else {
							packet.runtimeSeconds * 1000
						},
					)
				},
			)
			val sensorTemp = packet.sensorTemp
			if (sensorTemp != null) {
				receiver.getTracker(packet.hidId)?.context?.dispatch(TrackerActions.Update { copy(imuTemp = sensorTemp) })
			}
		}.launchIn(receiver.context.scope)
	}
}
