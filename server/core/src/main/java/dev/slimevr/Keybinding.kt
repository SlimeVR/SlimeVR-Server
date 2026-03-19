package dev.slimevr

import com.melloware.jintellitype.HotkeyListener
import com.melloware.jintellitype.JIntellitype
import dev.hannah.portals.PortalManager
import dev.hannah.portals.Shortcut
import dev.hannah.portals.globalShortcuts.ShortcutTuple
import dev.slimevr.config.KeybindingsConfig
import dev.slimevr.tracking.trackers.TrackerUtils
import io.eiren.util.OperatingSystem
import io.eiren.util.OperatingSystem.Companion.currentPlatform
import io.eiren.util.ann.AWTThread
import io.eiren.util.logging.LogManager

class Keybinding @AWTThread constructor(val server: VRServer) : HotkeyListener {
	val config: KeybindingsConfig = server.configManager.vrConfig.keybindings

	init {
		if (currentPlatform == OperatingSystem.WINDOWS) {
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

					val feetMountingResetBinding = config.feetMountingResetBinding
					JIntellitype.getInstance()
						.registerHotKey(FEET_MOUNTING_RESET, feetMountingResetBinding)
					LogManager.info("[Keybinding] Bound feet reset mounting to $feetMountingResetBinding")

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
		if (currentPlatform == OperatingSystem.LINUX) {
			val portalManager = PortalManager(SLIMEVR_IDENTIFIER)
			val fullReset = Shortcut("Full Reset", "CTRL+ALT+SHIFT+Y")
			val yawReset = Shortcut("Yaw Reset", "CTRL+ALT+SHIFT+U")
			val mountingReset = Shortcut("Mounting Reset", "CTRL+ALT+SHIFT+I")
			val feetMountingReset = Shortcut("Feet Mounting Reset","CTRL+ALT+SHIFT+P")
			val pauseTracking = Shortcut("Pause Tracking", "CTRL+ALT+SHIFT+O")
			val shortcutsList = mutableListOf(
				ShortcutTuple("FULL_RESET", fullReset.shortcut),
				ShortcutTuple("YAW_RESET", yawReset.shortcut),
				ShortcutTuple("MOUNTING_RESET", mountingReset.shortcut),
				ShortcutTuple("FEET_MOUNTING_RESET", feetMountingReset.shortcut),
				ShortcutTuple("PAUSE_TRACKING", pauseTracking.shortcut))
			val globalShortcutsHandler = portalManager.globalShortcutsRequest(shortcutsList)
			Runtime.getRuntime().addShutdownHook(Thread {
				println("Closing connection")
				globalShortcutsHandler.close()
			})

			globalShortcutsHandler.onShortcutActivated = { shortcutId ->
				when (shortcutId) {
					"FULL_RESET" -> {
						server.scheduleResetTrackersFull(RESET_SOURCE_NAME, config.fullResetDelay.toLong())
					}
					"YAW_RESET" -> {
						server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, config.yawResetDelay.toLong())
					}
					"MOUNTING_RESET" -> {
						server.scheduleResetTrackersMounting(
							RESET_SOURCE_NAME,
							config.mountingResetDelay.toLong(),
						)
					}
					"FEET_MOUNTING_RESET" -> {
						server.scheduleResetTrackersMounting(
							RESET_SOURCE_NAME,
							config.feetMountingResetDelay.toLong(),
							TrackerUtils.feetsBodyParts,
						)
					}
					"PAUSE_TRACKING" -> {
						server.scheduleTogglePauseTracking(
								RESET_SOURCE_NAME,
								config.pauseTrackingDelay.toLong(),
							)
					}
				}
			}
		}
	}

	@AWTThread
	override fun onHotKey(identifier: Int) {
		when (identifier) {
			FULL_RESET -> server.scheduleResetTrackersFull(RESET_SOURCE_NAME, config.fullResetDelay.toLong())

			YAW_RESET -> server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, config.yawResetDelay.toLong())

			MOUNTING_RESET -> server.scheduleResetTrackersMounting(
				RESET_SOURCE_NAME,
				config.mountingResetDelay.toLong(),
			)

			FEET_MOUNTING_RESET -> server.scheduleResetTrackersMounting(
				RESET_SOURCE_NAME,
				config.feetMountingResetDelay.toLong(),
				TrackerUtils.feetsBodyParts,
			)

			PAUSE_TRACKING ->
				server
					.scheduleTogglePauseTracking(
						RESET_SOURCE_NAME,
						config.pauseTrackingDelay.toLong(),
					)
		}
	}

	companion object {
		private const val RESET_SOURCE_NAME = "Keybinding"

		private const val FULL_RESET = 1
		private const val YAW_RESET = 2
		private const val MOUNTING_RESET = 3
		private const val FEET_MOUNTING_RESET = 4
		private const val PAUSE_TRACKING = 5
	}
}
