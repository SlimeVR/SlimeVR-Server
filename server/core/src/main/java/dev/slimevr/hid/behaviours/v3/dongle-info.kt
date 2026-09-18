package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDDongleInfo
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverActions
import dev.slimevr.hid.HIDReceiverBehaviour

class HIDDongleInfoBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDongleInfo> { packet ->
			receiver.context.dispatch(
				HIDReceiverActions.UpdateDongleInfo {
					when (packet) {
						is HIDDongleInfo.BasicInfo -> copy(hardwareAddress = packet.hwid, hardwareRevision = packet.hardwareRevision.toString())
						is HIDDongleInfo.Model -> copy(model = packet.value)
						is HIDDongleInfo.Manufacturer -> copy(manufacturer = packet.value)
						is HIDDongleInfo.FirmwareVersion -> copy(firmwareVersion = packet.value)
						is HIDDongleInfo.FirmwareDate -> copy(firmwareDate = packet.value)
						is HIDDongleInfo.CustomHardwareType -> copy(boardType = packet.value)
					}
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
