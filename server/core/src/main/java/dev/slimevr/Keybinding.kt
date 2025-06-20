package dev.slimevr

import com.melloware.jintellitype.HotkeyListener
import com.melloware.jintellitype.JIntellitype
import dev.slimevr.config.KeybindingsConfig
import io.eiren.util.OperatingSystem
import io.eiren.util.OperatingSystem.Companion.currentPlatform
import io.eiren.util.ann.AWTThread
import io.eiren.util.logging.LogManager

class Keybinding @AWTThread constructor(val server: VRServer) : HotkeyListener {
	val config: KeybindingsConfig = server.configManager.vrConfig.keybindings

	init {
		if (currentPlatform != OperatingSystem.WINDOWS) {
			LogManager
				.info(
					"[Keybinding] Currently only supported on Windows. Keybindings will be disabled.",
				)
		} else {
			try {
				if (JIntellitype.getInstance() != null) {
					JIntellitype.getInstance().addHotKeyListener(this)

					val fullResetBinding = config.fullResetBinding
					JIntellitype.getInstance()
						.registerHotKey(FULL_RESET, fullResetBinding)
					LogManager.info("[Keybinding] Bound full reset to $fullResetBinding")

					val yawResetBinding = config.yawResetBinding
					JIntellitype.getInstance()
						.registerHotKey(YAW_RESET, yawResetBinding)
					LogManager.info("[Keybinding] Bound yaw reset to $yawResetBinding")

					val mountingResetBinding = config.mountingResetBinding
					JIntellitype.getInstance()
						.registerHotKey(MOUNTING_RESET, mountingResetBinding)
					LogManager.info("[Keybinding] Bound reset mounting to $mountingResetBinding")

					val pauseTrackingBinding = config.pauseTrackingBinding
					JIntellitype.getInstance()
						.registerHotKey(PAUSE_TRACKING, pauseTrackingBinding)
					LogManager.info("[Keybinding] Bound pause tracking to $pauseTrackingBinding")
				}
			} catch (e: Throwable) {
				LogManager
					.warning(
						"[Keybinding] JIntellitype initialization failed. Keybindings will be disabled. Try restarting your computer.",
					)
			}
		}
	}

	@AWTThread
	override fun onHotKey(identifier: Int) {
		when (identifier) {
			FULL_RESET -> server.scheduleResetTrackersFull(RESET_SOURCE_NAME, config.fullResetDelay)

			YAW_RESET -> server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, config.yawResetDelay)

			MOUNTING_RESET -> server.scheduleResetTrackersMounting(
				RESET_SOURCE_NAME,
				config.mountingResetDelay,
			)

			PAUSE_TRACKING ->
				server
					.scheduleTogglePauseTracking(
						RESET_SOURCE_NAME,
						config.pauseTrackingDelay,
					)
		}
	}

	companion object {
		private const val RESET_SOURCE_NAME = "Keybinding"

		private const val FULL_RESET = 1
		private const val YAW_RESET = 2
		private const val MOUNTING_RESET = 3
		private const val PAUSE_TRACKING = 4
	}
}
