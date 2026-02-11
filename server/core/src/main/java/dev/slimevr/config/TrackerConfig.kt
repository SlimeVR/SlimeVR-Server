package dev.slimevr.config

import dev.slimevr.VRServer
import dev.slimevr.config.serializers.QuaternionSerializer
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.ObjectQuaternion
import kotlinx.serialization.Serializable

@Serializable
class TrackerConfig {
	var customName: String? = null
	var designation: String? = null

	@Serializable(with = QuaternionSerializer::class)
	var mountingOrientation: ObjectQuaternion? = null

	@Serializable(with = QuaternionSerializer::class)
	var mountingResetOrientation: ObjectQuaternion? = null

	/**
	 * Only checked if [ServerConfig.useMagnetometerOnAllTrackers] enabled
	 */
	var shouldHaveMagEnabled: Boolean? = null

	constructor(tracker: Tracker) {
		this.designation = if (tracker.trackerPosition != null) tracker.trackerPosition!!.designation else null
		this.customName = tracker.customName
		shouldHaveMagEnabled = tracker.isImu()
	}
}

// val Tracker.config: TrackerConfig
// 	get() = VRServer.instance.configManager.vrConfig.getTracker(this)
