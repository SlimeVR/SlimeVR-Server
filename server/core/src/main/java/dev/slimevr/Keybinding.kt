package dev.slimevr

import com.melloware.jintellitype.HotkeyListener
import com.melloware.jintellitype.JIntellitype
import dev.hannah.portals.PortalManager
import dev.hannah.portals.globalShortcuts.Shortcut
import dev.hannah.portals.globalShortcuts.ShortcutTuple
import dev.slimevr.config.KeybindingsConfig
import dev.slimevr.tracking.trackers.TrackerUtils
import io.eiren.util.OperatingSystem
import io.eiren.util.OperatingSystem.Companion.currentPlatform
import io.eiren.util.ann.AWTThread
import io.eiren.util.logging.LogManager
import solarxr_protocol.rpc.KeybindId

class Keybinding @AWTThread constructor(val server: VRServer) : HotkeyListener {
	val config: KeybindingsConfig = server.configManager.vrConfig.keybindings

	init {
		if (currentPlatform == OperatingSystem.WINDOWS) {
			try {
				if (JIntellitype.getInstance() != null) {
					JIntellitype.getInstance().addHotKeyListener(this)

					config.keybinds.forEach { (i, keybind) ->
						JIntellitype.getInstance()
							.registerHotKey(keybind.id, keybind.binding)
					}
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
			val feetMountingReset = Shortcut("Feet Mounting Reset", "CTRL+ALT+SHIFT+P")
			val pauseTracking = Shortcut("Pause Tracking", "CTRL+ALT+SHIFT+O")
			val shortcutsList = mutableListOf(
				ShortcutTuple("FULL_RESET", fullReset.shortcut),
				ShortcutTuple("YAW_RESET", yawReset.shortcut),
				ShortcutTuple("MOUNTING_RESET", mountingReset.shortcut),
				ShortcutTuple("FEET_MOUNTING_RESET", feetMountingReset.shortcut),
				ShortcutTuple("PAUSE_TRACKING", pauseTracking.shortcut),
			)
			val globalShortcutsHandler = portalManager.globalShortcutsRequest(shortcutsList)
			Runtime.getRuntime().addShutdownHook(
				Thread {
					println("Closing connection")
					globalShortcutsHandler.close()
				},
			)

			globalShortcutsHandler.onShortcutActivated = { shortcutId ->
				when (shortcutId) {
					"FULL_RESET" -> {
						val delay = config.keybinds[KeybindId.FULL_RESET]?.delay?.toLong() ?: 0L
						server.scheduleResetTrackersFull(RESET_SOURCE_NAME, delay)
					}

					"YAW_RESET" -> {
						val delay = config.keybinds[KeybindId.YAW_RESET]?.delay?.toLong() ?: 0L
						server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, delay)
					}

					"MOUNTING_RESET" -> {
						val delay = config.keybinds[KeybindId.MOUNTING_RESET]?.delay?.toLong() ?: 0L
						server.scheduleResetTrackersMounting(
							RESET_SOURCE_NAME,
							delay,
						)
					}

					"FEET_MOUNTING_RESET" -> {
						val delay = config.keybinds[KeybindId.FEET_MOUNTING_RESET]?.delay?.toLong() ?: 0L
						server.scheduleResetTrackersMounting(
							RESET_SOURCE_NAME,
							delay,
							TrackerUtils.feetsBodyParts,
						)
					}

					"PAUSE_TRACKING" -> {
						val delay = config.keybinds[KeybindId.PAUSE_TRACKING]?.delay?.toLong() ?: 0L
						server.scheduleTogglePauseTracking(
							RESET_SOURCE_NAME,
							delay,
						)
					}
				}
			}
		}
	}

	@AWTThread
	override fun onHotKey(identifier: Int) {
		when (identifier) {
			FULL_RESET -> {
				val delay = config.keybinds[KeybindId.FULL_RESET]?.delay?.toLong() ?: 0L
				server.scheduleResetTrackersFull(RESET_SOURCE_NAME, delay)
			}

			YAW_RESET -> {
				val delay = config.keybinds[KeybindId.YAW_RESET]?.delay?.toLong() ?: 0L
				server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, delay)
			}

			MOUNTING_RESET -> {
				val delay = config.keybinds[KeybindId.FEET_MOUNTING_RESET]?.delay?.toLong() ?: 0L
				server.scheduleResetTrackersMounting(
					RESET_SOURCE_NAME,
					delay,
					TrackerUtils.feetsBodyParts,
				)
			}

			FEET_MOUNTING_RESET -> {
				val delay = config.keybinds[KeybindId.FEET_MOUNTING_RESET]?.delay?.toLong() ?: 0L
				server.scheduleResetTrackersMounting(
					RESET_SOURCE_NAME,
					delay,
					TrackerUtils.feetsBodyParts,
				)
			}

			PAUSE_TRACKING -> {
				val delay = config.keybinds[KeybindId.PAUSE_TRACKING]?.delay?.toLong() ?: 0L
				server.scheduleTogglePauseTracking(
					RESET_SOURCE_NAME,
					delay,
				)
			}
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
