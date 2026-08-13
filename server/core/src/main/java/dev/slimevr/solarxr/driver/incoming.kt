package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeActions
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.driver_protocol.AddTrackerRequest
import solarxr_protocol.driver_protocol.AddTrackerResponse
import solarxr_protocol.driver_protocol.AddTrackerStatus
import solarxr_protocol.driver_protocol.HandshakeAvailable
import solarxr_protocol.driver_protocol.HandshakeResponse
import solarxr_protocol.driver_protocol.HandshakeStatus
import solarxr_protocol.driver_protocol.UpdateTrackerBattery
import solarxr_protocol.driver_protocol.UpdateTrackerPosition
import solarxr_protocol.driver_protocol.UpdateTrackerStatus

class DriverIncomingTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val server = appContext.server

		appContext.config.settings.context.state
			.map { it.data.driverConfig.enabled }
			.distinctUntilChanged()
			.onEach { enabled ->
				if (enabled) {
					receiver.sendDriverMessage(HandshakeAvailable())
				} else {
					val driverName = receiver.context.state.value.driverName ?: return@onEach
					val trackers = server.context.state.value.trackers.values.filter {
						val state = it.context.state.value
						state.origin == DeviceOrigin.DRIVER && state.driverName == driverName
					}
					trackers.forEach {
						it.context.dispatchAll(
							listOf(
								TrackerActions.SetDriverName(null),
								TrackerActions.SetStatus(TrackerStatus.DISCONNECTED),
							)
						)
					}

					receiver.context.dispatch(SolarXRBridgeActions.SetDriverInfo(null, null))
					receiver.sendDriverMessage(HandshakeResponse(status = HandshakeStatus.REJECTED_DISABLED))
				}
			}

		receiver.onDriverMessage<AddTrackerRequest> { req, replyTo ->
			val driverName = receiver.context.state.value.driverName
			val hardwareId = req.hardwareIdentifier

			if (hardwareId == null || driverName == null) {
				receiver.sendDriverMessage(AddTrackerResponse(status = AddTrackerStatus.ERROR), replyTo = replyTo)
				return@onDriverMessage
			}

			val existing = server.context.state.value.trackers.values
				.find { it.context.state.value.hardwareId == hardwareId }
			if (existing != null) {
				val trackerState = existing.context.state.value
				// Tracker is in use by another driver right now
				if (trackerState.driverName != null && trackerState.driverName != driverName) {
					receiver.sendDriverMessage(AddTrackerResponse(status = AddTrackerStatus.ERROR), replyTo = replyTo)
					return@onDriverMessage
				}

				existing.context.dispatchAll(
					listOf(
						TrackerActions.SetDriverName(driverName),
						TrackerActions.SetStatus(TrackerStatus.OK),
					)
				)
				receiver.sendDriverMessage(
					AddTrackerResponse(
						status = AddTrackerStatus.ALREADY_EXISTS,
						trackerId = trackerState.id.toUShort(),
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
				driverName = driverName,
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
				AddTrackerResponse(status = AddTrackerStatus.CREATED, trackerId = trackerId.toUShort()),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerStatus> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId ?: return@on
			val status = event.status ?: return@on
			server.getTracker(trackerId.toInt())?.context?.dispatch(TrackerActions.SetStatus(status))
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerBattery> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId ?: return@on
			val batteryLevel = event.batteryLevel ?: return@on
			val charging = event.charging ?: false
			val tracker = server.getTracker(trackerId.toInt()) ?: return@on
			val device = server.getDevice(tracker.context.state.value.deviceId) ?: return@on
			device.context.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = (batteryLevel / 100u).toFloat(), batteryVoltage = if (charging) 4.3f else 3.7f)
				},
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerPosition> { event ->
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
