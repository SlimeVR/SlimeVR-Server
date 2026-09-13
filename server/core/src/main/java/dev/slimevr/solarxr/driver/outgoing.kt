package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneId
import dev.slimevr.skeleton.boneId
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.createBone
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.driver_protocol.BoneBatteryUpdate
import solarxr_protocol.driver_protocol.SkeletonUpdate

val DRIVER_SUPPORTED_BONES = setOf(
	BodyPart.UPPER_CHEST,
	BodyPart.LEFT_UPPER_ARM,
	BodyPart.RIGHT_UPPER_ARM,
	BodyPart.HIP,
	BodyPart.LEFT_UPPER_LEG,
	BodyPart.RIGHT_UPPER_LEG,
	BodyPart.LEFT_FOOT,
	BodyPart.RIGHT_FOOT,
	BodyPart.LEFT_SHOULDER,
	BodyPart.RIGHT_SHOULDER,
	BodyPart.LEFT_HAND,
	BodyPart.RIGHT_HAND,
)

class DriverOutgoingTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	// Fallback chain per bone, used to attribute battery and status to the nearest
	// physical tracker. Bones without an entry just report no battery.
	val bodyPartToNearest: BodyPartMap<Set<BodyPart>> = BodyPartMap(
		mapOf(
			BodyPart.UPPER_CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST),
			BodyPart.HIP to setOf(BodyPart.HIP, BodyPart.LOWER_WAIST, BodyPart.UPPER_WAIST, BodyPart.LOWER_CHEST, BodyPart.UPPER_CHEST),
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

	private val nearestBoneIds: Map<BoneId, Set<BoneId>> = buildMap {
		bodyPartToNearest.forEachBone { bodyPart, fallbacks ->
			put(bodyPart.boneId, fallbacks.mapTo(mutableSetOf()) { it.boneId })
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: SolarXRBridge) {
		val server = appContext.server
		val settings = appContext.config.settings

		val boneBatteries = mutableMapOf<BoneId, BoneBatteryUpdate>()

		combine(settings.context.state.map { it.data.driverConfig }, receiver.context.state) { driverConfig, state ->
			Triple(
				driverConfig.enabled,
				state.driverName,
				if (!driverConfig.sendVelocity) state.boneMask?.copy(linearVelocity = false, angularVelocity = false) else state.boneMask,
			)
		}
			.distinctUntilChanged()
			.flatMapLatest { (enabled, driverName, boneMask) ->
				if (!enabled || driverName == null || boneMask == null) return@flatMapLatest emptyFlow()
				// Map the nearest trackers to their bone
				val trackerStateByBoneId = mutableMapOf<BoneId, TrackerState>()
				for (tracker in server.context.state.value.trackers.values) {
					val trackerState = tracker.context.state.value
					if (trackerState.origin == DeviceOrigin.DRIVER) continue
					val boneId = trackerState.boneId ?: continue
					trackerStateByBoneId.putIfAbsent(boneId, trackerState)
				}

				appContext.skeleton.computed.onEach { computedSkeleton ->
					val bones = computedSkeleton.entries.map { (id, bone) -> createBone(bone, id, boneMask) }

					receiver.sendDriverMessage(SkeletonUpdate(bones = bones))

					for (boneId in computedSkeleton.keys) {
						val closestTracker = nearestBoneIds[boneId].orEmpty()
							.firstNotNullOfOrNull { trackerStateByBoneId[it] }
						val closestDevice =
							server.context.state.value.devices[closestTracker?.deviceId]?.context?.state?.value

						if (closestDevice?.batteryLevel != null) {
							val battery = BoneBatteryUpdate(
								boneId = boneId.value,
								batteryLevel = (closestDevice.batteryLevel * 100).toUInt().toUByte(),
								charging = closestDevice.batteryVoltage != null && closestDevice.batteryVoltage >= 4.3f,
							)
							if (boneBatteries.put(boneId, battery) != battery) {
								AppLogger.solarxr.debug("Sending BoneBatteryUpdate for ${computedSkeleton.registry[boneId]?.key ?: boneId}")
								receiver.sendDriverMessage(battery)
							}
						}
					}
				}
			}.launchIn(receiver.context.scope)
	}
}
