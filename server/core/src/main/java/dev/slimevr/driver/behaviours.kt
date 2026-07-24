package dev.slimevr.driver

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus

class DriverOutgoingTrackersBehaviour : DriverBridgeBehaviour {
	// Keys should match with protocol.kt:bodyPartToRole
	val bodyPartToNearest: Map<BodyPart, Set<BodyPart>> = mapOf(
		BodyPart.UPPER_CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.CHEST),
		BodyPart.HIP to setOf(BodyPart.HIP, BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
		BodyPart.LEFT_UPPER_LEG to setOf(BodyPart.LEFT_UPPER_LEG),
		BodyPart.RIGHT_UPPER_LEG to setOf(BodyPart.RIGHT_UPPER_LEG),
		BodyPart.LEFT_FOOT to setOf(BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),
		BodyPart.RIGHT_FOOT to setOf(BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
		BodyPart.LEFT_UPPER_ARM to setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
		BodyPart.RIGHT_UPPER_ARM to setOf(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM),
		BodyPart.LEFT_HAND to setOf(BodyPart.LEFT_HAND, BodyPart.LEFT_LOWER_ARM),
		BodyPart.RIGHT_HAND to setOf(BodyPart.RIGHT_HAND, BodyPart.RIGHT_LOWER_ARM),
	)

	override fun observe(receiver: DriverBridge) {
		// Should be safe: StateFlow never delivers two emissions concurrently to the same collector.
		val subscribedTrackers = mutableSetOf<UByte>()

		combine(
			receiver.appContext.skeleton.computed,
			receiver.appContext.outputTrackerToggle.context.state.map { it.trackers },
			::Pair
		)
			.distinctUntilChanged()
			.onEach { (computedBones, enabledBodyParts) ->
			val serverState = receiver.appContext.server.context.state.value

			computedBones.forEach { (part, state) ->
				if (enabledBodyParts.contains(part)) {
					val closestTracker = bodyPartToNearest[part].orEmpty()
						.firstNotNullOfOrNull { fallbackPart ->
							serverState.trackers.values
								.find { it.context.state.value.bodyPart == fallbackPart && it.context.state.value.origin != DeviceOrigin.DRIVER }
								?.context?.state?.value
						}
					val closestDevice = serverState.devices[closestTracker?.deviceId]?.context?.state?.value

					val newTracker = subscribedTrackers.add(part.value)
					if (newTracker) {
						receiver.outbound.emit(
							DriverBridgeOutbound.TrackerAdded(
								trackerId = part.value.toInt(),
								part = part,
							),
						)
					}

					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerPosition(
							trackerId = part.value.toInt(),
							rotation = state.rotation,
							position = state.tailPosition,
						),
					)

					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							battery = closestDevice?.batteryLevel ?: 1f,
							charging = closestDevice?.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
							status = closestTracker?.status ?: TrackerStatus.OK,
						),
					)
				} else {
					receiver.outbound.emit(
						DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							battery = null,
							charging = false,
							status = TrackerStatus.DISCONNECTED,
						),
					)
				}
			}
		}.launchIn(receiver.context.scope)
	}
}

class DriverIncomingTrackersBehaviour : DriverBridgeBehaviour {
	override fun reduce(
		state: DriverBridgeState,
		action: DriverBridgeActions,
	): DriverBridgeState = when (action) {
		is DriverBridgeActions.AddTracker -> state.copy(trackers = state.trackers + (action.id to action.trackerId))
		is DriverBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version)
	}

	override fun observe(receiver: DriverBridge) {
		receiver.inbound.on<DriverBridgeInbound.Version> { event ->
			receiver.context.dispatch(DriverBridgeActions.UpdateProtocolVersion(event.protocolVersion))
		}.launchIn(receiver.context.scope)

		receiver.inbound.on<DriverBridgeInbound.TrackerAdded> { event ->
			handleTrackerAdded(
				receiver,
				event.id,
				event.name,
				event.manufacturer,
				event.serial,
			)
		}.launchIn(receiver.context.scope)

		receiver.inbound.on<DriverBridgeInbound.TrackerPosition> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.let { tracker ->
				tracker.context.dispatch( // TODO should maybe use TrackerActions.SetRotation?
					TrackerActions.Update {
						copy(rawRotation = event.rotation, rotation = event.rotation, position = event.position)
					},
				)
			}
		}.launchIn(receiver.context.scope)

		receiver.inbound.on<DriverBridgeInbound.TrackerBattery> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.let { tracker ->
				val device =
					tracker.appContext.server.getDevice(tracker.context.state.value.deviceId)
						?: error("could not find device")
				device.context.dispatch(
					DeviceActions.Update {
						copy(
							batteryLevel = event.batteryLevel / 100f,
							batteryVoltage = if (event.charging) 4.3f else 3.7f,
						)
					},
				)
			}
		}.launchIn(receiver.context.scope)
	}

	private fun handleTrackerAdded(
		receiver: DriverBridge,
		id: Int,
		name: String,
		manufacturer: String,
		serial: String,
	) {
		val server = receiver.appContext.server
		val scope = server.context.scope
		val existingTracker = server.context.state.value.trackers.values
			.find { tracker -> tracker.context.state.value.hardwareId == serial }

		val (device, tracker) = if (existingTracker != null) {
			val device = server.getDevice(existingTracker.context.state.value.deviceId)
				?: error("could not find existing device for serial $serial")
			Pair(device, existingTracker)
		} else {
			val deviceId = server.nextHandle()
			val newDevice = Device.create(
				scope = scope,
				appContext = receiver.appContext,
				id = deviceId,
				name = name,
				manufacturer = manufacturer,
				address = serial,
				macAddress = serial,
				origin = DeviceOrigin.DRIVER,
				protocolVersion = 0,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, newDevice))

			val trackerId = server.nextHandle()
			val tracker = Tracker.create(
				scope = scope,
				id = trackerId,
				name = name,
				deviceId = deviceId,
				sensorType = null,
				hardwareId = serial,
				origin = DeviceOrigin.DRIVER,
				appContext = receiver.appContext,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))

			Pair(newDevice, tracker)
		}

		receiver.context.dispatch(
			DriverBridgeActions.AddTracker(
				id,
				tracker.context.state.value.id,
			),
		)
		tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		device.context.dispatch(DeviceActions.Update { copy(protocolVersion = 0) })
	}
}
