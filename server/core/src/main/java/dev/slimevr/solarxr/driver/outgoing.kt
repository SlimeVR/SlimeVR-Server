package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.bones.BoneId
import dev.slimevr.logging.AppLogger
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
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.driver_protocol.BoneBatteryUpdate
import solarxr_protocol.driver_protocol.SkeletonUpdate

class DriverOutgoingTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
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
						val closestTracker = appContext.skeleton.definition.batterySourcesOf(boneId)
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
