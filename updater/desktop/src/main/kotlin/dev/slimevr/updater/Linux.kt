package dev.slimevr.updater

import dev.slimevr.updater.util.TerminalUtil
import kotlinx.io.IOException
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux(
	private val state: UpdaterState,
	private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateLinux(currentVersionTag: String, versionTag: String, configDir: String, vrConfig: String, serverUrl: String, openVRDriverUrl: String) {
		backupConfig(currentVersionTag, configDir, vrConfig)
		restoreConfig(versionTag, configDir, vrConfig)
		updateServer(serverUrl)
		updateLinuxSteamVRDriver(openVRDriverUrl)
	}

	fun backupConfig(versionTag: String, configDir: String, vrConfig: String) {
		TerminalUtil.info("Backing up Config")
		try {
			val targetDir =
				File(Paths.get(configDir, versionTag).toAbsolutePath().toString())
			targetDir.mkdirs()
			val config = File(vrConfig)
			val destination = "$targetDir/vrconfig.yml"
			config.copyTo(File(destination), true)
			TerminalUtil.success("Config backed up to $destination")
		} catch (e: IOException) {
			state.hasError = true
			state.errorText = "Error backing up config"
			TerminalUtil.error("Error backing up config")
		}
	}

	fun restoreConfig(versionTag: String, configDir: String, vrConfig: String) {
		try {
			val sourceDir =
				File(Paths.get(configDir, versionTag).toAbsolutePath().toString())
			if (!sourceDir.exists()) return
			val config = File("$sourceDir/vrconfig.yml")
			val destination = "$configDir/vrconfig.yml"
			config.copyTo(File(destination), true)
			TerminalUtil.success("Config restored up to $destination")
		} catch (e: IOException) {
			state.hasError = true
			state.errorText = "Error restoring config"
			TerminalUtil.error("Error restoring config")
		}
	}

	suspend fun updateLinuxSteamVRDriver(openVRDriverUrl: String) {
		TerminalUtil.info("Updating SteamVR Driver")
		state.update {
			statusText = "Updating SteamVR Driver"
			mainProgress = 0.5f
		}

		val steamVRPath =
			"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"

		val vrPathRegContents = io.executeShellCommand(steamVRPath)
		val isDriverRegistered = vrPathRegContents.contains("slimevr")

		if (isDriverRegistered) {
			state.update {
				statusText = "Updating OpenVR Driver"
				subText = "Downloading OpenVR Driver"
			}
			TerminalUtil.info("Downloading SteamVR Driver")

			io.downloadFile(openVRDriverUrl, LINUXSTEAMVRDRIVERNAME)

			state.update {
				subText = "Unzipping SteamVR Driver"
				subProgressisVisible = true
			}
			TerminalUtil.info("Unzipping SteamVR Driver")

			io.unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)

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

		val vrPathRegContents = io.executeShellCommand(steamVRPath)
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

	suspend fun updateServer(serverUrl: String) {
		TerminalUtil.info("Updating server")
		state.update {
			statusText = "Updating Server"
			subProgressisVisible = true
			subProgress = 0f
		}

		state.update {
			subText = "Downloading Server"
		}
		TerminalUtil.info("Downloading server")
		io.downloadFile(serverUrl, LINUXSERVERNAME)

		state.update {
			subProgress = 1f
			statusText = "Server download complete"
			subText = ""
		}
		TerminalUtil.success("Updating server done")
	}

	// Legacy
	suspend fun feeder() {
		state.update {
			statusText = "Downloading Feeder App"
		}

		io.downloadFile(LINUXFEEDERURL, LINUXFEEDERNAME)

		state.update {
			statusText = "Unzipping Feeder App"
		}

		io.unzip(LINUXFEEDERNAME, LINUXFEEDERDIRECTORY)

		state.update {
			statusText = "Installing Feeder App"
		}

		io.executeShellCommand(
			"chmod",
			"+x",
			"$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App",
		)

		io.executeShellCommand(
			"$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App",
			"--install",
		)

		state.update {
			mainProgress = 1f
			statusText = "Feeder App Done"
		}
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

		val res = io.executeShellCommand(
			"pkexec",
			"cp",
			"$path/69-slimevr-devices.rules",
			"/etc/udev/rules.d/69-slimevr-devices.rules",
		)

		state.update {
			statusText =
				if (res.contains("Error")) {
					"Error installing udev rules"
				} else {
					"Udev rules installed"
				}

			mainProgress = 0.66f
		}
	}

	companion object {
		private const val LINUXCONFIGLOCATION =
			""

		private const val LINUXSTEAMVRDRIVERURL =
			"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"

		private const val LINUXSTEAMVRDRIVERNAME =
			"slimevr-openvr-driver-x64-linux.zip"

		private const val LINUXSTEAMVRDRIVERDIRECTORY =
			"slimevr-openvr-driver-x64-linux"

		private const val LINUXFEEDERURL =
			"https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-Linux.zip"

		private const val LINUXFEEDERNAME =
			"SlimeVR-Feeder-App-Linux.zip"

		private const val LINUXFEEDERDIRECTORY =
			"SlimeVR-Feeder-App-Linux"

		private const val LINUXSERVERNAME =
			"SlimeVR-amd64.appimage"
	}
}
