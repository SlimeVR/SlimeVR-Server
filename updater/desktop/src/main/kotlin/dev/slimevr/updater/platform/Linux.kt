package dev.slimevr.updater.platform

import dev.slimevr.updater.utils.TerminalUtil
import dev.slimevr.updater.updater.UpdaterIO
import dev.slimevr.updater.gui.UpdaterState
import dev.slimevr.updater.gui.update
import kotlinx.io.IOException
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists
import dev.slimevr.updater.updater.Constants.Companion
import dev.slimevr.updater.updater.Constants.Companion.LINUXFEEDERDIRECTORY
import dev.slimevr.updater.updater.Constants.Companion.LINUXFEEDERNAME
import dev.slimevr.updater.updater.Constants.Companion.LINUXFEEDERURL
import dev.slimevr.updater.updater.Constants.Companion.LINUXSERVERNAME
import dev.slimevr.updater.updater.Constants.Companion.LINUXSTEAMVRDRIVERDIRECTORY
import dev.slimevr.updater.updater.Constants.Companion.LINUXSTEAMVRDRIVERNAME

class Linux(
    private val state: UpdaterState,
    private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateLinux(currentVersionTag: String, versionTag: String, configDir: String, vrConfig: String, serverUrl: String, serverChecksum: String, openVRDriverUrl: String) {
		io.backupConfig(currentVersionTag, configDir, vrConfig)
		io.restoreConfig(versionTag, configDir, vrConfig)
		updateServer(serverUrl, serverChecksum)
		updateLinuxSteamVRDriver(openVRDriverUrl)
	}



	suspend fun updateLinuxSteamVRDriver(openVRDriverUrl: String) {
		TerminalUtil.info("Updating SteamVR Driver")
		state.update {
			statusText = "Updating SteamVR Driver"
			mainProgress = 0.5f
		}

		val steamVRPath =
			"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"

		val (_, vrPathRegContents) = io.executeShellCommand(steamVRPath) ?: run {
			TerminalUtil.warn("Failed to check SteamVR path configuration.")
			return
		}
		val isDriverRegistered = vrPathRegContents.contains("slimevr")

		if (isDriverRegistered) {
			state.update {
				statusText = "Updating OpenVR Driver"
				subText = "Downloading OpenVR Driver"
			}
			TerminalUtil.info("Downloading SteamVR Driver")

			io.downloadFile(openVRDriverUrl, LINUXSTEAMVRDRIVERNAME, "")

			state.update {
				subText = "Unzipping SteamVR Driver"
				subProgressIsVisible = true
			}
			TerminalUtil.info("Unzipping SteamVR Driver")

			io.unzip(LINUXSTEAMVRDRIVERNAME)

			state.update {
				statusText = "Registering SteamVR Driver"
			}

			io.executeShellCommand(
				steamVRPath,
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		}

		state.update {
			mainProgress = 1.0f
			statusText = "SteamVR Driver done"
		}
		TerminalUtil.success("SteamVR Driver done")
	}

	suspend fun removeLinuxSteamVRDriver() {
		val steamVRPath =
			"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"

		val (_, vrPathRegContents) = io.executeShellCommand(steamVRPath) ?: run {
			state.update { statusText = "Failed to run vrpathreg script" }
			return
		}
		val isDriverRegistered = vrPathRegContents.contains("slimevr")

		if (isDriverRegistered) {
			state.update {
				statusText = "Removing SteamVR driver"
			}

			io.executeShellCommand(
				steamVRPath,
				"removedriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			state.update {
				statusText = "SteamVR driver not registered. Skipping..."
			}
		}
	}

	suspend fun updateServer(serverUrl: String, serverChecksum: String) {
		TerminalUtil.info("Updating server")
		state.update {
			statusText = "Updating Server"
			subProgressIsVisible = true
			subProgress = 0f
		}

		state.update {
			subText = "Downloading Server"
		}
		TerminalUtil.info("Downloading server")
		io.downloadFile(serverUrl, LINUXSERVERNAME, serverChecksum)

		state.update {
			subProgress = 1f
			statusText = "Server download complete"
			subText = ""
		}
		TerminalUtil.success("Updating server done")

		val command = listOf("chmod", "+x", LINUXSERVERNAME)

		io.unzip("slimevr-openvr-driver-x64-linux.zip")

		//TerminalUtil.info(io.executeShellCommand("chmod +x ${LINUXSERVERNAME}"))

	}

	suspend fun updateUdev() {
		state.update {
			statusText = "Setting up udev rules"
		}

		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")

		if (file.exists()) {
			state.update {
				statusText = "Udev rules already installed"
				mainProgress = 0.66f
			}
			return
		}

		state.update {
			statusText = "Requesting privileges"
		}

		val (_, res) = io.executeShellCommand(
			"pkexec",
			"cp",
			"$path/69-slimevr-devices.rules",
			"/etc/udev/rules.d/69-slimevr-devices.rules",
		) ?: run {
			state.update {
				statusText = "Error executing privilege command"
				mainProgress = 0.66f
			}
			return
		}

		state.update {
			statusText =
				if (res.contains("Error", ignoreCase = true)) {
					"Error installing udev rules"
				} else {
					"Udev rules installed"
				}

			mainProgress = 0.66f
		}
	}
}
