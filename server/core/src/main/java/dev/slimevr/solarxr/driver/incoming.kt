package dev.slimevr.solarxr.driver

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.driver_protocol.AddTrackerStatus
import solarxr_protocol.driver_protocol.InboundAddTrackerRequest
import solarxr_protocol.driver_protocol.InboundAddTrackerResponse
import solarxr_protocol.driver_protocol.InboundBatteryNotification
import solarxr_protocol.driver_protocol.InboundTrackerPositionNotification
import solarxr_protocol.driver_protocol.InboundTrackerStatusNotification

class DriverIncomingTrackersBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.onDriverMessage<InboundAddTrackerRequest> { req, replyTo ->
			val hardwareId = req.hardwareId

			if (hardwareId == null || receiver.context.state.value.driverName == null) {
				receiver.sendDriverMessage(InboundAddTrackerResponse(status = AddTrackerStatus.ERROR), replyTo = replyTo)
				return@onDriverMessage
			}

			val existing = server.context.state.value.trackers.values
				.find { it.context.state.value.hardwareId == hardwareId }
			if (existing != null) {
				receiver.sendDriverMessage(
					InboundAddTrackerResponse(
						status = AddTrackerStatus.ALREADY_EXISTS,
						trackerId = existing.context.state.value.id.toUShort(),
					),
					replyTo = replyTo,
				)
				return@onDriverMessage
			}

			val scope = server.context.scope
			val deviceId = server.nextHandle()
			val device = Device.create(
				scope = scope,
				appContext = receiver.appContext,
				id = deviceId,
				name = req.displayName ?: "Tracker",
				manufacturer = req.manufacturer ?: "SlimeVR",
				address = hardwareId,
				macAddress = hardwareId,
				origin = DeviceOrigin.DRIVER,
				protocolVersion = 0,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, device))

			val trackerId = server.nextHandle()
			val tracker = Tracker.create(
				scope = scope,
				id = trackerId,
				name = req.displayName ?: "Tracker #$trackerId",
				bodyPart = req.bodyPart,
				deviceId = deviceId,
				hardwareId = hardwareId,
				origin = DeviceOrigin.DRIVER,
				appContext = receiver.appContext,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))

			receiver.sendDriverMessage(
				InboundAddTrackerResponse(status = AddTrackerStatus.CREATED, trackerId = trackerId.toUShort()),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<InboundTrackerStatusNotification> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId ?: return@on
			val status = event.status ?: return@on
			server.getTracker(trackerId.toInt())?.context?.dispatch(TrackerActions.SetStatus(status))
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<InboundBatteryNotification> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId ?: return@on
			val batteryLevel = event.batteryLevel ?: return@on
			val charging = event.charging ?: false
			val tracker = server.getTracker(trackerId.toInt()) ?: return@on
			val device = server.getDevice(tracker.context.state.value.deviceId) ?: return@on
			device.context.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = batteryLevel / 100f, batteryVoltage = if (charging) 4.3f else 3.7f)
				},
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<InboundTrackerPositionNotification> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId ?: return@on
			val rotation = event.rotation ?: return@on

			server.getTracker(trackerId.toInt())?.context?.dispatch(
				TrackerActions.SetRotation(
					rotation = Quaternion(rotation.w, rotation.x, rotation.y, rotation.z),
					position = event.position?.let { Vector3(it.x, it.y, it.z) },
					// TODO: send velocity?
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
