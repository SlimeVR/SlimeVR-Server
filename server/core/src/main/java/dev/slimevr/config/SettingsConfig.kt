package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class SettingsConfig(
	val server: ServerConfig = ServerConfig(),
	val filters: FiltersConfig = FiltersConfig(),
	val oscRouter: OSCConfig = OSCConfig(),
	val vrcOSC: VRCOSCConfig = VRCOSCConfig(),
	val vmc: VMCConfig = VMCConfig(),
	val autoBone: AutoBoneConfig = AutoBoneConfig(),
	val keybindings: KeybindingsConfig = KeybindingsConfig(),
	val legTweaks: LegTweaksConfig = LegTweaksConfig(),
	val tapDetection: TapDetectionConfig = TapDetectionConfig(),
	val resetsConfig: ResetsConfig = ResetsConfig(),
	val stayAlignedConfig: StayAlignedConfig = StayAlignedConfig(),
	val hidConfig: HIDConfig = HIDConfig(),
	val bridges: Map<String, BridgeConfig> = HashMap(),
	val knownDevices: Set<String> = setOf(),
	val overlay: OverlayConfig = OverlayConfig(),
	val trackingChecklist: TrackingChecklistConfig = TrackingChecklistConfig(),
	val vrcConfig: VRCConfig = VRCConfig(),
	val version: Int = CONFIG_VERSION,
) {
// 	fun getTracker(tracker: Tracker): TrackerConfig {
// 		var config = trackers[tracker.name]
// 		if (config == null) {
// 			config = TrackerConfig(tracker)
// 			trackers[tracker.name] = config
// 		}
// 		return config
// 	}
//
// 	fun readTrackerConfig(tracker: Tracker) {
// 		if (tracker.userEditable) {
// 			val config = getTracker(tracker)
// 			tracker.readConfig(config)
// 			tracker.resetsHandler.readResetConfig(resetsConfig)
// 			if (tracker.allowReset) {
// 				tracker.saveMountingResetOrientation(config)
// 			}
// 			if (tracker.allowFiltering) {
// 				tracker
// 					.filteringHandler
// 					.readFilteringConfig(filters, tracker.getRotation())
// 			}
// 		}
// 	}
//
// 	fun writeTrackerConfig(tracker: Tracker?) {
// 		if (tracker?.userEditable == true) {
// 			val tc = getTracker(tracker)
// 			tracker.writeConfig(tc)
// 		}
// 	}

// 	fun getBridge(bridgeKey: String): BridgeConfig {
// 		var config = bridges[bridgeKey]
// 		if (config == null) {
// 			config = BridgeConfig()
// 			bridges[bridgeKey] = config
// 		}
// 		return config
// 	}

	fun isKnownDevice(mac: String?): Boolean = knownDevices.contains(mac)

// 	fun addKnownDevice(mac: String): Boolean = knownDevices.add(mac)

// 	fun forgetKnownDevice(mac: String): Boolean = knownDevices.remove(mac)

	companion object {
		const val CONFIG_VERSION = 15
	}
}
