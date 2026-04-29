package dev.slimevr.updater

import java.nio.file.Paths

class Windows(
	private val state: UpdaterState,
	private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateWindows(serverUrl: String) {
		usbDrivers()
		steamVRDriver()
		feeder()
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

		io.downloadFile(serverUrl, WINDOWSSERVERNAME)

		state.update {
			subProgress = 1f
			statusText = "Server download complete"
		}
	}

	fun usbDrivers() {
		state.statusText = "Checking Windows USB Drivers"

		val installedDriversList =
			io.executeShellCommand("powershell.exe", "pnputil /enum-drivers")

		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			state.statusText = "USB drivers already installed"
			state.mainProgress = 0.33f
			return
		}

		state.statusText = "Installing USB drivers"

		val driverInstallOutput =
			io.executeShellCommand("$path\\installusbdrivers.bat")

		state.statusText =
			if (driverInstallOutput.contains("error", ignoreCase = true)) {
				"Driver install failed"
			} else {
				"USB drivers installed"
			}

		state.mainProgress = 0.33f
	}

	suspend fun feeder() {
		state.statusText = "Downloading Feeder App"

		io.downloadFile(
			WINDOWSFEEDERURL,
			WINDOWSFEEDERNAME,
		)

		state.statusText = "Unzipping Feeder App"

		io.unzip(
			WINDOWSFEEDERNAME,
			WINDOWSFEEDERDIRECTORY,
		)

		state.statusText = "Installing Feeder App"

		io.executeShellCommand(
			"${path}\\${WINDOWSFEEDERDIRECTORY}\\SlimeVR-Feeder-App.exe",
			"--install",
		)

		state.mainProgress = 1f
		state.statusText = "Feeder App Done"
	}

	suspend fun steamVRDriver() {
		state.statusText = "Checking SteamVR"

		val steamVRLocation = io.executeShellCommand(
			"powershell.exe",
			"-Command",
			"(Get-ItemProperty 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820').InstallLocation",
		).trim()

		if (!steamVRLocation.contains("SteamVR")) {
			state.statusText = "SteamVR not installed"
			return
		}

		val vrPathRegContents = io.executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"finddriver",
			"slimevr",
		)

		val isDriverRegistered =
			vrPathRegContents.contains(WINDOWSSTEAMVRDRIVERDIRECTORY)

		if (isDriverRegistered) {
			state.statusText = "SteamVR driver already registered"
			return
		}

		state.statusText = "Downloading SteamVR Driver"

		io.downloadFile(
			WINDOWSSTEAMVRDRIVERURL,
			WINDOWSSTEAMVRDRIVERNAME,
		)

		state.statusText = "Unzipping SteamVR Driver"

		io.unzip(
			WINDOWSSTEAMVRDRIVERNAME,
			WINDOWSSTEAMVRDRIVERDIRECTORY,
		)

		state.statusText = "Registering SteamVR Driver"

		io.executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"adddriver",
			"$path\\$WINDOWSSTEAMVRDRIVERDIRECTORY\\slimevr",
		)

		state.mainProgress = 0.66f
		state.statusText = "SteamVR Driver done"
	}

	companion object {

		private const val WINDOWSSTEAMVRDRIVERURL =
			"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"

		private const val WINDOWSSTEAMVRDRIVERNAME =
			"slimevr-openvr-driver-win64.zip"

		private const val WINDOWSSTEAMVRDRIVERDIRECTORY =
			"slimevr-openvr-driver-win64"

		private const val WINDOWSFEEDERURL =
			"https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-win64.zip"

		private const val WINDOWSFEEDERNAME =
			"SlimeVR-Feeder-App-win64.zip"

		private const val WINDOWSFEEDERDIRECTORY =
			"SlimeVR-Feeder-App-win64"

		private const val WINDOWSSERVERURL =
			"https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-win64.zip"

		private const val WINDOWSSERVERNAME =
			"slimevr-win64.zip"

		private const val WINDOWSSERVERDIRECTORY =
			"slimevr-win64"
	}
}
