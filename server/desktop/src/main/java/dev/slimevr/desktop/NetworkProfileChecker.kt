package dev.slimevr.desktop

import dev.slimevr.VRServer
import io.eiren.util.OperatingSystem
import solarxr_protocol.rpc.StatusData
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusPublicNetworkT
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

enum class NetworkProfile {
	PRIVATE,
	PUBLIC,
}

fun checkNetworkProfile(): NetworkProfile? {
	if (OperatingSystem.currentPlatform != OperatingSystem.WINDOWS) {
		return null
	}
	try {
		// Full command as a single string
		val command = "powershell.exe -Command \"(Get-NetConnectionProfile).NetworkCategory\""

		// Use ProcessBuilder with the full command passed through the shell
		val processBuilder = ProcessBuilder("cmd.exe", "/c", command)
		processBuilder.redirectErrorStream(true)
		val process = processBuilder.start()

		// Capture the output
		val output = BufferedReader(InputStreamReader(process.inputStream)).useLines { lines ->
			lines.joinToString("\n")
		}
		val exitCode = process.waitFor()
		if (exitCode != 0) {
			return null
		}
		return when (output.trim()) {
			"Private" -> NetworkProfile.PRIVATE
			"Public" -> NetworkProfile.PUBLIC
			else -> null
		}
	} catch (e: Exception) {
		return null
	}
}

class NetworkProfileChecker(private val server: VRServer) {
	private val updateTickTimer = Timer("NetworkProfileCheck")
	private var lastPublicNetworkStatus: UInt = 0u
	private var currentNetworkProfile: NetworkProfile? = null

	init {
		if (OperatingSystem.currentPlatform == OperatingSystem.WINDOWS) {
			this.updateTickTimer.scheduleAtFixedRate(0, 3000) {
				val profile = checkNetworkProfile()
				if (profile != currentNetworkProfile) {
					currentNetworkProfile = profile
					if (lastPublicNetworkStatus == 0u && profile == NetworkProfile.PUBLIC) {
						lastPublicNetworkStatus = server.statusSystem.addStatus(
							StatusDataUnion().apply {
								type = StatusData.StatusPublicNetwork
								value = StatusPublicNetworkT()
							},
							false,
						)
					} else if (lastPublicNetworkStatus != 0u && profile != NetworkProfile.PUBLIC) {
						server.statusSystem.removeStatus(lastPublicNetworkStatus)
						lastPublicNetworkStatus = 0u
					}
				}
			}
		}
	}
}
