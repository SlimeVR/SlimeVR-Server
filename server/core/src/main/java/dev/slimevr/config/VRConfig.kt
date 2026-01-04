package dev.slimevr.config

import dev.slimevr.tracking.trackers.Tracker
import kotlinx.serialization.Serializable

@Serializable
class VRConfig {
	val server: ServerConfig = ServerConfig()

	val filters: FiltersConfig = FiltersConfig()

	val oscRouter: OSCConfig = OSCConfig()

	val vrcOSC: VRCOSCConfig = VRCOSCConfig()

	val vmc: VMCConfig = VMCConfig()

	val autoBone: AutoBoneConfig = AutoBoneConfig()

	val keybindings: KeybindingsConfig = KeybindingsConfig()

	val skeleton: SkeletonConfig = SkeletonConfig()

	val legTweaks: LegTweaksConfig = LegTweaksConfig()

	val tapDetection: TapDetectionConfig = TapDetectionConfig()

	val resetsConfig: ResetsConfig = ResetsConfig()

	val stayAlignedConfig = StayAlignedConfig()

	private val trackers: MutableMap<String, TrackerConfig> = HashMap()

	private val bridges: MutableMap<String, BridgeConfig> = HashMap()

	val knownDevices: MutableSet<String> = mutableSetOf()

	val overlay: OverlayConfig = OverlayConfig()

	val trackingChecklist: TrackingChecklistConfig = TrackingChecklistConfig()

	val vrcConfig: VRCConfig = VRCConfig()

	val modelVersion: String = CONFIG_VERSION.toString()

	fun getTracker(tracker: Tracker): TrackerConfig {
		var config = trackers[tracker.name]
		if (config == null) {
			config = TrackerConfig(tracker)
			trackers[tracker.name] = config
		}
		return config
	}

	fun readTrackerConfig(tracker: Tracker) {
		if (tracker.userEditable) {
			val config = getTracker(tracker)
			tracker.readConfig(config)
			tracker.resetsHandler.readResetConfig(resetsConfig)
			if (tracker.allowReset) {
				tracker.saveMountingResetOrientation(config)
			}
			if (tracker.allowFiltering) {
				tracker
					.filteringHandler
					.readFilteringConfig(filters, tracker.getRotation())
			}
		}
	}

	fun writeTrackerConfig(tracker: Tracker?) {
		if (tracker?.userEditable == true) {
			val tc = getTracker(tracker)
			tracker.writeConfig(tc)
		}
	}

	fun getBridge(bridgeKey: String): BridgeConfig {
		var config = bridges[bridgeKey]
		if (config == null) {
			config = BridgeConfig()
			bridges[bridgeKey] = config
		}
		return config
	}

	fun isKnownDevice(mac: String?): Boolean = knownDevices.contains(mac)

	fun addKnownDevice(mac: String): Boolean = knownDevices.add(mac)

	fun forgetKnownDevice(mac: String): Boolean = knownDevices.remove(mac)

	companion object {
		const val CONFIG_VERSION = 14
	}
}
