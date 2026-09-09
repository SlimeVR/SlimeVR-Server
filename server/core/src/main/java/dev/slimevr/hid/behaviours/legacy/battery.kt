@file:Suppress("DEPRECATION") // legacy v2 packets

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.device.DeviceActions
import dev.slimevr.hid.HIDDataLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationBatteryLegacy
import dev.slimevr.hid.HIDRotationButtonLegacy
import dev.slimevr.hid.HIDRuntimeLegacy

class HIDBatteryBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDRotationBatteryLegacy> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			device.context.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = packet.batteryLevel, batteryVoltage = packet.batteryVoltage, signalStrength = packet.rssi)
				},
			)
			device.recordRssi(packet.rssi)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRotationButtonLegacy> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			device.context.dispatch(DeviceActions.Update { copy(signalStrength = packet.rssi) })
			device.recordRssi(packet.rssi)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDDataLegacy> { packet ->
			val device = receiver.getDevice(packet.hidId) ?: return@on
			device.context.dispatch(DeviceActions.Update { copy(signalStrength = packet.rssi) })
			device.recordRssi(packet.rssi)
		}.launchIn(receiver.context.scope)

		receiver.packetEvents.on<HIDRuntimeLegacy> { packet ->
			// -1: not yet known (keep existing value); 0: N/A (e.g. charging)
			if (packet.runtime >= 0) {
				receiver.getDevice(packet.hidId)?.context?.dispatch(
					DeviceActions.Update { copy(batteryRemainingRuntime = packet.runtime) },
				)
			}
		}.launchIn(receiver.context.scope)
	}
}
