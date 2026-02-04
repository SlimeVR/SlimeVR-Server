package dev.slimevr.desktop.platform.linux

import java.io.IOException

public class InstallLinux {

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

	fun setSteamVRDriver() {

	}

	fun setUDEVRules() {
		//check if udev is active
		//check if udes rules already exist
		//add if they don't exist

	}

}
