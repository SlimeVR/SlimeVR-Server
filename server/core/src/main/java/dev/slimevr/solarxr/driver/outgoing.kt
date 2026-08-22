package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.createBone
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f
import solarxr_protocol.driver_protocol.BoneBatteryUpdate
import solarxr_protocol.driver_protocol.BoneStatusUpdate
import solarxr_protocol.driver_protocol.SkeletonUpdate
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
		val server = appContext.server
		val settings = appContext.config.settings

		val boneStatuses = bodyPartMap<TrackerStatus>()
		val boneBatteries = bodyPartMap<BoneBatteryUpdate>()

		combine(settings.context.state.map { it.data.driverConfig.enabled }, receiver.context.state) { enabled, state ->
			Triple(
				enabled,
				state.driverName,
				state.boneMask
			)
		}
			.distinctUntilChanged()
			.flatMapLatest { (enabled, driverName, boneMask) ->
				if (!enabled || driverName == null || boneMask == null) return@flatMapLatest emptyFlow()
				// Map the nearest trackers to their body parts
				val trackerStateByBodyPart = bodyPartMap<TrackerState>()
				for (tracker in server.context.state.value.trackers.values) {
					val trackerState = tracker.context.state.value
					if (trackerState.origin == DeviceOrigin.DRIVER) continue
					val bodyPart = trackerState.bodyPart ?: continue
					trackerStateByBodyPart.putIfAbsent(bodyPart, trackerState)
				}

				appContext.skeleton.computed.onEach { computedSkeleton ->
					val bones = buildList {
						for (boneState in computedSkeleton.values) {
							add(createBone(boneState, boneMask))
						}
					}

					computedSkeleton.forEach { (bodyPart, boneState) ->
						val closestTracker = bodyPartToNearest[bodyPart].orEmpty()
							.firstNotNullOfOrNull { fallbackPart -> trackerStateByBodyPart[fallbackPart] }
						val closestDevice =
							server.context.state.value.devices[closestTracker?.deviceId]?.context?.state?.value

						val status = closestTracker?.status ?: TrackerStatus.OK
						if (boneStatuses.put(bodyPart, status) != status) {
							AppLogger.solarxr.debug("Sending BoneStatusUpdate for $bodyPart")
							receiver.sendDriverMessage(BoneStatusUpdate(bone = bodyPart, status = status))
						}

						val battery = BoneBatteryUpdate(
							bone = bodyPart,
							batteryLevel = closestDevice?.batteryLevel ?: 1f,
							charging = closestDevice?.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
						)
						if (boneBatteries.put(bodyPart, battery) != battery) {
							AppLogger.solarxr.debug("Sending BoneBatteryUpdate for $bodyPart")
							receiver.sendDriverMessage(battery)
						}
					}

					receiver.sendDriverMessage(SkeletonUpdate(bones = bones))
				}
			}.launchIn(receiver.context.scope)
	}
}
