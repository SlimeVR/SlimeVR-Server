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

enum class Keybinds(val id: Int, val keybindName: String, val description: String, val keybind: String) {
	FULL_RESET(KeybindId.FULL_RESET, "FULL_RESET", "Full Reset", "CTRL+ALT+SHIFT+Y"),
	MOUNTING_RESET(KeybindId.MOUNTING_RESET, "MOUNTING_RESET", "Mounting Reset", "CTRL+ALT+SHIFT+I"),
	PAUSE_TRACKING(KeybindId.PAUSE_TRACKING, "PAUSE_TRACKING", "Pause Tracking", "CTRL+ALT+SHIFT+O"),
	YAW_RESET(KeybindId.YAW_RESET,"YAW_RESET", "Yaw Reset", "CTRL+ALT+SHIFT+U"),
	FEET_MOUNTING_RESET(KeybindId.FEET_MOUNTING_RESET,"FEET_MOUNTING_RESET", "Feet Mounting Reset", "CTRL+ALT+SHIFT+P");

	override fun toString(): String {
		return keybindName
	}

	companion object {
		private val byId = Keybinds.entries.associateBy { it.id }
		private val byName = Keybinds.entries.associateBy { it.keybindName }

		fun getById(value: Int): Keybinds? = byId[value]
		fun getByName(name: String): Keybinds? = byName[name]
	}
}

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
			val shortcutsList = Keybinds.entries.map {
				ShortcutTuple(it.name, Shortcut(it.description, it.keybind).shortcut)
			}.toMutableList()

			val globalShortcutsHandler = portalManager.globalShortcutsRequest(shortcutsList)
			Runtime.getRuntime().addShutdownHook(
				Thread {
					println("Closing connection")
					globalShortcutsHandler.close()
				},
			)

			val onShortcut: (id: String) -> Unit = { shortcutId ->
				val keybind = Keybinds.getByName(shortcutId);
				if (keybind != null) {
					val delay = config.keybinds[keybind]?.delay?.toLong() ?: 0L
					when (keybind) {
						Keybinds.FULL_RESET -> {
							server.scheduleResetTrackersFull(keybind.keybindName, delay)
						}
						Keybinds.YAW_RESET -> {
							server.scheduleResetTrackersYaw(keybind.keybindName, delay)
						}
						Keybinds.MOUNTING_RESET  -> {
							server.scheduleResetTrackersMounting(
								keybind.keybindName,
								delay,
							)
						}
						Keybinds.FEET_MOUNTING_RESET-> {
							server.scheduleResetTrackersMounting(
								keybind.keybindName,
								delay,
								TrackerUtils.feetsBodyParts,
							)
						}
						Keybinds.PAUSE_TRACKING -> {
							server.scheduleTogglePauseTracking(
								keybind.keybindName,
								delay,
							)
						}
					}
				}
			}

			globalShortcutsHandler.onShortcutActivated = onShortcut
		}
	}

	@AWTThread
	override fun onHotKey(identifier: Int) {
		val keybind = Keybinds.getById(identifier) ?: return
		val delay = config.keybinds[keybind]?.delay?.toLong() ?: 0L;
		when (keybind) {
			Keybinds.FULL_RESET -> {
				server.scheduleResetTrackersFull(keybind.keybindName, delay)
			}

			Keybinds.YAW_RESET -> {
				server.scheduleResetTrackersYaw(keybind.keybindName, delay)
			}

			Keybinds.MOUNTING_RESET -> {
				server.scheduleResetTrackersMounting(
					keybind.keybindName,
					delay,
					TrackerUtils.feetsBodyParts,
				)
			}

			Keybinds.FEET_MOUNTING_RESET -> {
				server.scheduleResetTrackersMounting(
					keybind.keybindName,
					delay,
					TrackerUtils.feetsBodyParts,
				)
			}

			Keybinds.PAUSE_TRACKING -> {
				server.scheduleTogglePauseTracking(
					keybind.keybindName,
					delay,
				)
			}
		}
	}
}
