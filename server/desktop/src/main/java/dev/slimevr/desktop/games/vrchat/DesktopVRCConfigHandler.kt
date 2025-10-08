package dev.slimevr.desktop.games.vrchat

import dev.slimevr.games.vrchat.VRCAvatarMeasurementType
import dev.slimevr.games.vrchat.VRCConfigHandler
import dev.slimevr.games.vrchat.VRCConfigValues
import dev.slimevr.games.vrchat.VRCSpineMode
import dev.slimevr.games.vrchat.VRCTrackerModel
import io.eiren.util.OperatingSystem
import java.util.Timer
import kotlin.concurrent.timerTask

const val VRC_REG_PATH = "Software\\VRChat\\VRChat"

class DesktopVRCConfigHandler : VRCConfigHandler() {

	private val getDevicesTimer = Timer("FetchVRCConfigTimer")
	private val regEdit: AbstractRegEdit =
		if (OperatingSystem.currentPlatform == OperatingSystem.WINDOWS) {
			RegEditWindows()
		} else {
			RegEditLinux()
		}

	private var configState: VRCConfigValues? = null
	private var vrcConfigKeys: Map<String, String>
	lateinit var onChange: (config: VRCConfigValues) -> Unit

	private fun intValue(key: String): Int? {
		val realKey = vrcConfigKeys[key] ?: return null
		return regEdit.getDwordValue(VRC_REG_PATH, realKey)
	}

	private fun doubleValue(key: String): Double? {
		val realKey = vrcConfigKeys[key] ?: return null
		return regEdit.getQwordValue(VRC_REG_PATH, realKey)
	}

	init {
		vrcConfigKeys = if (OperatingSystem.currentPlatform == OperatingSystem.WINDOWS ||
			OperatingSystem.currentPlatform == OperatingSystem.LINUX
		) {
			regEdit.getVRChatKeys(VRC_REG_PATH)
		} else {
			mapOf()
		}
	}

	private fun updateCurrentState() {
		vrcConfigKeys = regEdit.getVRChatKeys(VRC_REG_PATH)
		val newConfig = VRCConfigValues(
			legacyMode = intValue("VRC_IK_LEGACY") == 1,
			shoulderTrackingDisabled = intValue("VRC_IK_DISABLE_SHOULDER_TRACKING") == 1,
			userHeight = doubleValue("PlayerHeight") ?: -1.0,
			calibrationRange = doubleValue("VRC_IK_CALIBRATION_RANGE") ?: -1.0,
			trackerModel = VRCTrackerModel.getByValue(intValue("VRC_IK_TRACKER_MODEL") ?: -1) ?: VRCTrackerModel.UNKNOWN,
			spineMode = VRCSpineMode.getByValue(intValue("VRC_IK_FBT_SPINE_MODE") ?: -1) ?: VRCSpineMode.UNKNOWN,
			calibrationVisuals = intValue("VRC_IK_CALIBRATION_VIS") == 1,
			avatarMeasurementType = VRCAvatarMeasurementType.getByValue(intValue("VRC_IK_AVATAR_MEASUREMENT_TYPE") ?: -1) ?: VRCAvatarMeasurementType.UNKNOWN,
			shoulderWidthCompensation = intValue("VRC_IK_SHOULDER_WIDTH_COMPENSATION") == 1,
		)
		if (newConfig != configState) {
			configState = newConfig
			onChange(newConfig)
		}
	}

	override val isSupported: Boolean
		get() = (
			OperatingSystem.currentPlatform == OperatingSystem.WINDOWS ||
				OperatingSystem.currentPlatform == OperatingSystem.LINUX
			) &&
			vrcConfigKeys.isNotEmpty()

	override fun initHandler(onChange: (config: VRCConfigValues) -> Unit) {
		this.onChange = onChange
		if (isSupported) {
			updateCurrentState()
			getDevicesTimer.scheduleAtFixedRate(
				timerTask {
					updateCurrentState()
				},
				0,
				3000,
			)
		}
	}
}
