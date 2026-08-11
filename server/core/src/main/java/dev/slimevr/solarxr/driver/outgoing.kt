package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f
import solarxr_protocol.driver_protocol.AddTrackerStatus
import solarxr_protocol.driver_protocol.OutboundAddTrackerRequest
import solarxr_protocol.driver_protocol.OutboundAddTrackerResponse
import solarxr_protocol.driver_protocol.OutboundTrackerPositionNotification
import solarxr_protocol.driver_protocol.OutboundTrackerStatusNotification
import solarxr_protocol.rpc.RoutingOutput
import java.util.concurrent.ConcurrentHashMap

class DriverOutgoingTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
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

	override fun observe(receiver: SolarXRBridge) {
		val requestedTrackers = ConcurrentHashMap.newKeySet<UByte>()
		val confirmedTrackers = ConcurrentHashMap.newKeySet<UByte>()

		// Status and battery are rebuilt every frame but only change every few seconds, so the driver
		// was being sent the same values at skeleton rate. Keep the last one per body part and only
		// send on a real change, the same way subscribedTrackers already gates TrackerAdded.
		val lastStatus = mutableMapOf<UByte, OutboundTrackerStatusNotification>()

		combine(
			appContext.skeleton.computed,
			appContext.boneRouting.context.state
				.map { state -> state.routes.filterValues { RoutingOutput.DRIVER in it }.keys }
				.distinctUntilChanged(),
			::Pair,
		)
			.distinctUntilChanged()
			.onEach { (computedSkeleton, enabledBodyParts) ->
				if (receiver.context.state.value.driverName == null) return@onEach

				val serverState = appContext.server.context.state.value

				// Map the nearest trackers to their body parts
				val trackerStateByBodyPart = bodyPartMap<TrackerState>()
				for (tracker in serverState.trackers.values) {
					val trackerState = tracker.context.state.value
					if (trackerState.origin == DeviceOrigin.DRIVER) continue
					val bodyPart = trackerState.bodyPart ?: continue
					trackerStateByBodyPart.putIfAbsent(bodyPart, trackerState)
				}

				computedSkeleton.forEach { (part, state) ->
					if (enabledBodyParts.contains(part)) {
						val closestTracker = bodyPartToNearest[part].orEmpty()
							.firstNotNullOfOrNull { fallbackPart -> trackerStateByBodyPart[fallbackPart] }
						val closestDevice = serverState.devices[closestTracker?.deviceId]?.context?.state?.value

						if (requestedTrackers.add(part.value)) {
							receiver.context.scope.launch {
								try {
									val response = receiver.requestDriverMessage<OutboundAddTrackerResponse>(
										OutboundAddTrackerRequest(trackerId = part.value.toUShort(), bodyPart = part),
									)
									if (response.status == AddTrackerStatus.ERROR) {
										AppLogger.solarxr.warn("Driver rejected adding tracker for body part $part")
										requestedTrackers.remove(part.value) // Should we retry on the next frame?
									} else {
										confirmedTrackers.add(part.value)
									}
								} catch (e: TimeoutCancellationException) {
									AppLogger.solarxr.warn("Timeout waiting for driver to add tracker for body part $part")
									requestedTrackers.remove(part.value)
								}
							}
						}

						if (part.value !in confirmedTrackers) return@forEach

						receiver.sendDriverMessage(
							OutboundTrackerPositionNotification(
								trackerId = part.value.toUShort(),
								rotation = Quat(state.rotation.x, state.rotation.y, state.rotation.z, state.rotation.w),
								position = Vec3f(state.tailPosition.x, state.tailPosition.y, state.tailPosition.z),
								// TODO add veliocity data
							),
						)

						val status = OutboundTrackerStatusNotification(
							trackerId = part.value.toUShort(),
							status = closestTracker?.status ?: TrackerStatus.OK,
							batteryLevel = closestDevice?.batteryLevel ?: 1f,
							charging = closestDevice?.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
						)
						if (lastStatus.put(part.value, status) != status) receiver.sendDriverMessage(status)
					} else {
						if (part.value !in confirmedTrackers) return@forEach

						val status = OutboundTrackerStatusNotification(
							trackerId = part.value.toUShort(),
							status = TrackerStatus.DISCONNECTED,
							batteryLevel = null,
							charging = false,
						)
						if (lastStatus.put(part.value, status) != status) receiver.sendDriverMessage(status)
					}
				}
			}.launchIn(receiver.context.scope)
	}
}
