package dev.slimevr.desktop.games.vrchat

import com.sun.jna.Memory
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import com.sun.jna.ptr.IntByReference
import dev.slimevr.games.vrchat.*
import io.eiren.util.OperatingSystem
import java.util.*
import kotlin.concurrent.timerTask

// Vrchat is dumb and write 64 bit doubles in the registry as DWORD instead of QWORD.
// so we have to be creative
fun getQwordValue(path: String, key: String): Double? {
	val hKey = WinReg.HKEY_CURRENT_USER
	val phkResult = WinReg.HKEYByReference()

	// Open the registry key
	if (Advapi32.INSTANCE.RegOpenKeyEx(hKey, path, 0, WinNT.KEY_READ, phkResult) != 0) {
		println("Error: Cannot open registry key")
		return null
	}

	val lpData = Memory(8)
	val lpcbData = IntByReference(8)

	val result = Advapi32.INSTANCE.RegQueryValueEx(
		phkResult.value, key, 0, null, lpData, lpcbData
	)
	Advapi32.INSTANCE.RegCloseKey(phkResult.value)

	if (result != 0) {
		println("Error: Cannot read registry key")
		return null
	}
	return lpData.getDouble(0)
}

fun getDwordValue(path: String, key: String): Int? {
	return try {
		val data = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, path, key)
		data
	} catch (e: Exception) {
		println("Error reading DWORD: ${e.message}")
		null
	}
}

fun getVRChatKeys(path: String): Map<String, String> {
	val keysMap = mutableMapOf<String, String>()

	try {
		Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, path).forEach {
			keysMap[it.key.replace("""_h\d+$""".toRegex(), "")] = it.key
		}
	} catch (e: Exception) {
		println("Error reading Values from VRC registry: ${e.message}")
	}
	return keysMap
}


const val VRC_REG_PATH = "Software\\VRChat\\VRChat"

class DesktopVRCConfigHandler: VRCConfigHandler() {

	private val getDevicesTimer = Timer("FetchVRCConfigTimer")

	private var configState: VRCConfigValues? = null
	private var vrcConfigKeys = getVRChatKeys(VRC_REG_PATH);
	lateinit var onChange: (config: VRCConfigValues) -> Unit

	private fun intValue(key: String): Int? {
		val realKey = vrcConfigKeys[key] ?: return null;
		return getDwordValue(VRC_REG_PATH, realKey)
	}

	private fun doubleValue(key: String): Double? {
		val realKey = vrcConfigKeys[key] ?: return null;
		return getQwordValue(VRC_REG_PATH, realKey)
	}

	private fun updateCurrentState() {
		vrcConfigKeys = getVRChatKeys(VRC_REG_PATH)
		val newConfig = VRCConfigValues(
			legacyMode = intValue("VRC_IK_LEGACY_CALIBRATION") == 1,
			shoulderTrackingDisabled = intValue("VRC_IK_DISABLE_SHOULDER_TRACKING") == 1,
			userHeight = doubleValue("PlayerHeight") ?: -1.0,
			calibrationRange = doubleValue("VRC_IK_CALIBRATION_RANGE") ?: -1.0,
			trackerModel =  VRCTrackerModel.getByValue(intValue("VRC_IK_TRACKER_MODEL") ?: -1) ?: VRCTrackerModel.UNKNOWN,
			spineMode = VRCSpineMode.getByValue(intValue("VRC_IK_FBT_SPINE_MODE") ?: -1)  ?: VRCSpineMode.UNKNOWN,
			calibrationVisuals =  intValue("VRC_IK_CALIBRATION_VIS") == 1,
			avatarMeasurementType = VRCAvatarMeasurementType.getByValue(intValue("VRC_IK_AVATAR_MEASUREMENT_TYPE") ?: -1)  ?: VRCAvatarMeasurementType.UNKNOWN
		)
		if (newConfig != configState) {
			configState = newConfig;
			onChange(newConfig);
		}
	}

	override val isSupported: Boolean
		get() = OperatingSystem.currentPlatform === OperatingSystem.WINDOWS && vrcConfigKeys.isNotEmpty()

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
