package dev.slimevr.desktop.vrchat

import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.config.AppConfig
import dev.slimevr.vrchat.VRCConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.emptyFlow
import solarxr_protocol.rpc.VRCAvatarMeasurementType
import solarxr_protocol.rpc.VRCConfigValues
import solarxr_protocol.rpc.VRCSpineMode
import solarxr_protocol.rpc.VRCTrackerModel

internal const val VRC_REG_PATH = "Software\\VRChat\\VRChat"

fun createDesktopVRCConfigManager(config: AppConfig, scope: CoroutineScope): VRCConfigManager = when (CURRENT_PLATFORM) {
	Platform.WINDOWS -> VRCConfigManager.create(
		config = config,
		scope = scope,
		isSupported = true,
		values = windowsVRCConfigFlow(),
	)

	Platform.LINUX -> VRCConfigManager.create(
		config = config,
		scope = scope,
		isSupported = true,
		values = linuxVRCConfigFlow(),
	)

	else -> VRCConfigManager.create(
		config = config,
		scope = scope,
		isSupported = false,
		values = emptyFlow(),
	)
}

internal suspend fun buildVRCConfigValues(
	intValue: suspend (String) -> Int?,
	doubleValue: suspend (String) -> Double?,
): VRCConfigValues = VRCConfigValues(
	legacyMode = intValue("VRC_IK_LEGACY") == 1,
	shoulderTrackingDisabled = intValue("VRC_IK_DISABLE_SHOULDER_TRACKING") == 1,
	shoulderWidthCompensation = intValue("VRC_IK_SHOULDER_WIDTH_COMPENSATION") == 1,
	userHeight = doubleValue("PlayerHeight")?.toFloat() ?: -1.0f,
	calibrationRange = doubleValue("VRC_IK_CALIBRATION_RANGE")?.toFloat() ?: -1.0f,
	trackerModel = when (intValue("VRC_IK_TRACKER_MODEL")) {
		0 -> VRCTrackerModel.SPHERE
		1 -> VRCTrackerModel.SYSTEM
		2 -> VRCTrackerModel.BOX
		3 -> VRCTrackerModel.AXIS
		else -> VRCTrackerModel.UNKNOWN
	},
	spineMode = when (intValue("VRC_IK_FBT_SPINE_MODE")) {
		0 -> VRCSpineMode.LOCK_HIP
		1 -> VRCSpineMode.LOCK_HEAD
		2 -> VRCSpineMode.LOCK_BOTH
		else -> VRCSpineMode.UNKNOWN
	},
	calibrationVisuals = intValue("VRC_IK_CALIBRATION_VIS") == 1,
	avatarMeasurementType = when (intValue("VRC_IK_AVATAR_MEASUREMENT_TYPE")) {
		0 -> VRCAvatarMeasurementType.ARM_SPAN
		1 -> VRCAvatarMeasurementType.HEIGHT
		else -> VRCAvatarMeasurementType.UNKNOWN
	},
)
