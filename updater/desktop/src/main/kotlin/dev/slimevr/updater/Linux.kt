package dev.slimevr.updater

import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux(
	private val state: UpdaterState,
	private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateLinux() {
		updateUdev()
		feeder()
	}

	suspend fun updateLinuxSteamVRDriver(openVRDriverUrl: String) {
		state.update {
			statusText = "Updating SteamVR Driver"
		}

		val steamVRPath =
			"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"

		val vrPathRegContents = io.executeShellCommand(steamVRPath)
		val isDriverRegistered = vrPathRegContents.contains("slimevr")

		if (isDriverRegistered) {
			state.update {
				statusText = "Downloading SteamVR Driver"
			}

			io.downloadFile(openVRDriverUrl, LINUXSTEAMVRDRIVERNAME)

			state.update {
				statusText = "Unzipping SteamVR Driver"
			}

			io.unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)

			state.update {
				statusText = "Registering SteamVR Driver"
			}

			io.executeShellCommand(
				steamVRPath,
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			state.update {
				statusText = "SteamVR driver already registered. Skipping..."
			}
		}

		state.update {
			mainProgress = 0.33f
			statusText = "SteamVR Driver done"
		}
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
		println("downloading server")
		state.update {
			statusText = "Updating SlimeVR"
			subProgress = 0f
		}

		state.update {
			statusText = "Downloading Server"
		}

		io.downloadFile(serverUrl, LINUXSERVERNAME)

		state.update {
			subProgress = 1f
			statusText = "Server download complete"
		}
	}

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

		private const val LINUXSERVERURL =
			"https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-amd64.appimage"

		private const val LINUXSERVERNAME =
			"SlimeVR-amd64.appimage"
	}
}
