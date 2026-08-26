package dev.slimevr.driver

import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.RoutingOutput

class DriverOutgoingTrackersBehaviour : DriverBridgeBehaviour {
	// Fallback chain per bone, used to attribute battery and status to the nearest
	// physical tracker. Bones without an entry just report no battery.
	val bodyPartToNearest: BodyPartMap<Set<BodyPart>> = BodyPartMap(
		mapOf(
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
		),
	)

	override fun observe(receiver: DriverBridge) {
		// Should be safe: StateFlow never delivers two emissions concurrently to the same collector.
		val subscribedTrackers = mutableSetOf<UByte>()

		// Status and battery are rebuilt every frame but only change every few seconds, so the driver
		// was being sent the same values at skeleton rate. Keep the last one per body part and only
		// send on a real change, the same way subscribedTrackers already gates TrackerAdded.
		val lastStatus = mutableMapOf<UByte, DriverBridgeOutbound.TrackerStatus>()

		combine(
			receiver.appContext.skeleton.computed,
			receiver.appContext.boneRouting.context.state
				.map { state -> state.routes.filterValues { RoutingOutput.DRIVER in it }.keys }
				.distinctUntilChanged(),
			::Pair,
		)
			.distinctUntilChanged()
			.onEach { (computedSkeleton, enabledBodyParts) ->
				val serverState = receiver.appContext.server.context.state.value

				// Map the nearest trackers to their body parts
				val trackerStateByBodyPart = bodyPartMap<TrackerState>()
				for (tracker in serverState.trackers.values) {
					val trackerState = tracker.context.state.value
					if (trackerState.origin == DeviceOrigin.DRIVER) continue
					val bodyPart = trackerState.bodyPart ?: continue
					trackerStateByBodyPart.putIfAbsent(bodyPart, trackerState)
				}

				computedSkeleton.forEachBone { part, state ->
					if (enabledBodyParts.contains(part)) {
						val closestTracker = bodyPartToNearest[part].orEmpty()
							.firstNotNullOfOrNull { fallbackPart -> trackerStateByBodyPart[fallbackPart] }
						val closestDevice = serverState.devices[closestTracker?.deviceId]?.context?.state?.value

						val newTracker = subscribedTrackers.add(part.value)
						if (newTracker) {
							// FIXME : sometimes doesn't work when launching SteamVR after SlimeVR
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

						val status = DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							battery = closestDevice?.batteryLevel ?: 1f,
							charging = closestDevice?.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
							status = closestTracker?.status ?: TrackerStatus.OK,
						)
						if (lastStatus.put(part.value, status) != status) receiver.outbound.emit(status)
					} else {
						val status = DriverBridgeOutbound.TrackerStatus(
							trackerId = part.value.toInt(),
							battery = null,
							charging = false,
							status = TrackerStatus.DISCONNECTED,
						)
						if (lastStatus.put(part.value, status) != status) receiver.outbound.emit(status)
					}
				}
			}.launchIn(receiver.context.scope)
	}
}

class DriverIncomingTrackersBehaviour : DriverBridgeBehaviour {
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
				event.bodyPart,
			)
		}.launchIn(receiver.context.scope)

		receiver.inbound.on<DriverBridgeInbound.TrackerStatus> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.context?.dispatch(
				TrackerActions.SetStatus(status = event.status),
			)
		}.launchIn(receiver.context.scope)

		receiver.inbound.on<DriverBridgeInbound.TrackerPosition> { event ->
			val trackerId = receiver.context.state.value.trackers[event.id] ?: return@on
			receiver.appContext.server.getTracker(trackerId)?.setRotation(rotation = event.rotation, position = event.position)
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
		bodyPart: BodyPart?,
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
				bodyPart = bodyPart,
				deviceId = deviceId,
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
