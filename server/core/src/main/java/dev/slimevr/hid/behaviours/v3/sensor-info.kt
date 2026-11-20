package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDSensorInfo
import dev.slimevr.tracker.TrackerActions
import solarxr_protocol.datatypes.BodyPart

class HIDSensorInfoBehaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDSensorInfo> { packet ->
			if (packet.sensorId != 0) return@on
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			val bodyPart = packet.defaultBodyPosition.takeIf { it != 0 }?.let { BodyPart.fromValue(it.toUByte()) }
			tracker.context.dispatch(
				TrackerActions.Update {
					copy(
						imuType = packet.imuType,
						magStatus = packet.magStatus,
						completedRestCalibration = true,
						intendedBodyPart = bodyPart ?: intendedBodyPart,
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
