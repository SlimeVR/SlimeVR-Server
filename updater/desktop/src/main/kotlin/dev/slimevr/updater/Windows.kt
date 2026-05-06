package dev.slimevr.updater

import kotlinx.io.IOException
import java.io.File
import java.nio.file.Paths

class Windows(
	private val state: UpdaterState,
	private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateWindows(currentVersionTag: String, versionTag: String, configDir: String, vrConfig: String, serverUrl: String, openVRDriverUrl: String) {
		backupConfig(currentVersionTag, configDir, vrConfig)
		restoreConfig(versionTag, configDir, vrConfig)
		updateServer(serverUrl)
		usbDrivers()
		steamVRDriver()
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

		io.unzip(WINDOWSSERVERNAME)
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
			WINDOWSSTEAMVRDRIVERNAME
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
