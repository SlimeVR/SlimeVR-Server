package dev.slimevr.updater.platform

import com.sun.jna.platform.win32.WinReg
import dev.slimevr.updater.utils.TerminalUtil
import dev.slimevr.updater.updater.UpdaterIO
import dev.slimevr.updater.gui.UpdaterState
import dev.slimevr.updater.gui.update
import dev.slimevr.updater.updater.Constants.Companion.WINDOWSSERVERNAME
import dev.slimevr.updater.updater.Constants.Companion.WINDOWSSTEAMVRDRIVERDIRECTORY
import dev.slimevr.updater.updater.Constants.Companion.WINDOWSSTEAMVRDRIVERNAME
import dev.slimevr.updater.updater.Constants.Companion.WINDOWSSTEAMVRDRIVERURL
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Windows(
    private val state: UpdaterState,
    private val io: UpdaterIO,
) {

	private val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateWindows(
		currentVersionTag: String,
		versionTag: String,
		configDir: String,
		vrConfig: String,
		serverUrl: String,
		serverChecksum: String,
		openVRDriverUrl: String
	) {
		io.backupConfig(currentVersionTag, configDir, vrConfig)
		io.restoreConfig(versionTag, configDir, vrConfig)
		updateServer(serverUrl, serverChecksum)
		usbDrivers()
		steamVRDriver()
	}

	suspend fun updateServer(serverUrl: String, serverChecksum: String) {
		println("downloading server")
		state.update {
			statusText = "Updating SlimeVR"
			subProgress = 0f
		}

		state.update {
			statusText = "Downloading Server"
		}

		io.downloadFile(serverUrl, WINDOWSSERVERNAME, serverChecksum)

		state.update {
			subProgress = 1f
			statusText = "Server download complete"
		}

		io.unzip(WINDOWSSERVERNAME)
	}

	fun usbDrivers() {
		state.statusText = "Checking Windows USB Drivers"

		val (_, installedDriversList) = io.executeShellCommand("powershell.exe", "pnputil /enum-drivers")
			?: run {
				state.statusText = "Failed to check USB drivers"
				return
			}

		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			state.statusText = "USB drivers already installed"
			state.mainProgress = 0.33f
			return
		}

		state.statusText = "Installing USB drivers"

		val (_, driverInstallOutput) = io.executeShellCommand("$path\\installusbdrivers.bat")
			?: run {
				state.statusText = "Driver install script failed to execute"
				return
			}

		state.statusText =
			if (driverInstallOutput.contains("error", ignoreCase = true)) {
				"Driver install failed"
			} else {
				"USB drivers installed"
			}

		state.mainProgress = 0.33f
	}

	suspend fun steamVRDriver() {
		state.statusText = "Checking SteamVR"
		val regEdit = RegEdit()
		val steamVRLocation = regEdit.getKeyByPath(
			WinReg.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820"
		)["InstallLocation"]
		if (steamVRLocation == null || !steamVRLocation.endsWith("SteamVR")) {
			TerminalUtil.warn("SteamVR driver installation failed: couldn't find SteamVR")
			return
		}

		if (Path(steamVRLocation, "drivers", "slimevr").exists()) {
			TerminalUtil.warn("Skipping SteamVR driver installation: driver is in SteamVR drivers folder")
			TerminalUtil.warn("If you would like the SteamVR driver to automatically update, please uninstall the other version of SlimeVR")
			return
		}

		val pathRegPath = "$steamVRLocation\\bin\\win64\\vrpathreg.exe"
		val (findExitCode, _) = io.executeShellCommand(
			pathRegPath,
			"finddriver",
			"slimevr"
		) ?: run {
			TerminalUtil.warn("SteamVR driver installation failed: couldn't run vrpathreg finddriver")
			return
		}

		if (!io.shouldInstallDriver(findExitCode)) {
			TerminalUtil.info(
				"Skipping SteamVR driver installation: ${
					io.getDriverInstallSkipReason(
						findExitCode
					)
				}"
			)
			return
		}

		state.statusText = "Downloading SteamVR Driver"

		io.downloadFile(
			WINDOWSSTEAMVRDRIVERURL,
			WINDOWSSTEAMVRDRIVERNAME,
			""
		)

		state.statusText = "Unzipping SteamVR Driver"

		io.unzip(
			WINDOWSSTEAMVRDRIVERNAME
		)

		state.statusText = "Registering SteamVR Driver"

		val (addExitCode, _) = io.executeShellCommand(
			pathRegPath,
			"adddriver",
			"$path\\$WINDOWSSTEAMVRDRIVERDIRECTORY"
		) ?: run {
			TerminalUtil.warn("SteamVR driver installation failed: couldn't run vrpathreg adddriver")
			return
		}

		if (addExitCode != 0) {
			TerminalUtil.warn("SteamVR driver installation failed: vrpathreg exited with code $addExitCode")
			return
		}

		state.mainProgress = 0.66f
		state.statusText = "SteamVR Driver done"

		TerminalUtil.info("SteamVR driver successfully installed")
	}
}
