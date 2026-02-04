package dev.slimevr.desktop.platform.windows

import java.io.IOException
import java.nio.file.Paths

class InstallWindows {

	fun DoWindowsCheck() {
		CheckIfUSBDriversInstalled()
		RegisterSteamDriver()
	}


	fun executeShellCommand(command: String): String {
		return try {
			val process = ProcessBuilder(*command.split(" ").toTypedArray())
				.redirectErrorStream(true)
				.start()
			process.inputStream.bufferedReader().readText().also {
				process.waitFor()
			}
		} catch (e: IOException) {
			"Error executing command: ${e.message}"
		}
	}

	fun CheckIfUSBDriversInstalled() {
		val installedDriversList = executeShellCommand("powershell.exe  pnputil /enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")
		val path = Paths.get("").toAbsolutePath().toString()

		if (ch341ser && ch343ser && silabser) {
			println("drivers already installed!")
		} else {
			println("Cannot find one of the drivers, installing drivers")
			val driverinstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
			println(driverinstallOutput)

		}
	}


	fun CheckIfSteamVRDriversInstalled() {

	}

	fun RegisterSteamDriver() {
		val path = Paths.get("").toAbsolutePath().toString()
		val driverInstallOutput = executeShellCommand("powershell.exe $path\\steamvr.ps1")
		println(driverInstallOutput)
	}
}
