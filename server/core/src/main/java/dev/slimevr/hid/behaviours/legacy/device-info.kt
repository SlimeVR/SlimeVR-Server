@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDDeviceInfoLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.behaviours.ensureHidTracker
import dev.slimevr.tracker.TrackerActions
import solarxr_protocol.datatypes.TrackerStatus

class HIDDeviceInfoBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDeviceInfoLegacy> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on

			device.context.dispatch(
				DeviceActions.Update {
					copy(
						boardType = packet.boardType,
						mcuType = packet.mcuType,
						firmwareVersion = packet.firmwareVersion,
						firmwareDate = packet.firmwareDate,
						batteryLevel = packet.batteryLevel,
						batteryVoltage = packet.batteryVoltage,
						signalStrength = packet.rssi,
					)
				},
			)
			device.recordRssi(packet.rssi)

			val tracker = ensureHidTracker(receiver, packet.hidId, device.context.state.value.id, imuType = packet.imuType) ?: return@on
			// legacy carries imu/mag type in every type-0, so keep re-asserting them
			tracker.context.dispatch(TrackerActions.Update { copy(imuType = packet.imuType, magStatus = packet.magStatus) })
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		}.launchIn(receiver.context.scope)
	}
}
