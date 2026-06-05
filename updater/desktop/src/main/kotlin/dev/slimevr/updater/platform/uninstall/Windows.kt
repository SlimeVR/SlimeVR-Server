package dev.slimevr.updater.platform.uninstall

import com.sun.jna.platform.win32.WinReg
import dev.slimevr.updater.currentPath
import dev.slimevr.updater.gui.UpdaterState
import dev.slimevr.updater.platform.RegEdit
import dev.slimevr.updater.updater.Constants.Companion.WINDOWSSTEAMVRDRIVERDIRECTORY
import dev.slimevr.updater.updater.UpdaterIO
import dev.slimevr.updater.utils.TerminalUtil
import kotlin.text.endsWith

class Windows(
	private val state: UpdaterState,
	private val io: UpdaterIO,
) {

	fun uninstall() {

	}


	fun removeServer(versionTag: String) {
		state.statusText = "Removing server"
		io.deleteDirectory("${currentPath}/${versionTag}")
	}

	fun removeSteamVRDriver() {
		state.statusText = "Removing SteamVRDriver"
		val regEdit = RegEdit()
		val steamVRLocation = regEdit.getKeyByPath(
			WinReg.HKEY_LOCAL_MACHINE,
			"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820"
		)["InstallLocation"]
		if (steamVRLocation == null || !steamVRLocation.endsWith("SteamVR")) {
			TerminalUtil.warn("SteamVR driver installation failed: couldn't find SteamVR")
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

		val (addExitCode, _) = io.executeShellCommand(
			pathRegPath,
			"removeddriver",
			"$currentPath\\driver\\$WINDOWSSTEAMVRDRIVERDIRECTORY"
		) ?: run {
			TerminalUtil.warn("SteamVR driver installation failed: couldn't run vrpathreg adddriver")
			return
		}
	}

	fun removeFeeder() {

	}

}
