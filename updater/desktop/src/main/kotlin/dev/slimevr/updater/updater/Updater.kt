package dev.slimevr.updater.updater

import dev.slimevr.updater.utils.TerminalUtil
import dev.slimevr.updater.platform.OperatingSystem.Companion.currentPlatform
import dev.slimevr.updater.gui.UpdaterState
import dev.slimevr.updater.platform.Linux
import dev.slimevr.updater.platform.OperatingSystem
import dev.slimevr.updater.platform.Windows
import dev.slimevr.updater.updater.UpdaterController.Companion.launchServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.system.exitProcess

const val SLIMEVR_IDENTIFIER = "dev.slimevr.SlimeVR"
val os = OperatingSystem

class Updater(
	val state: UpdaterState,
	val updaterIO: UpdaterIO,
) {
	 suspend fun runUpdater() {
		val vrConfig = resolveConfig("vrconfig.yml")
		val configDir =
			resolveConfigDirectory(SLIMEVR_IDENTIFIER)?.toAbsolutePath().toString()
		TerminalUtil.info("Using config dir: $configDir")
		val arch = System.getProperty("os.arch").lowercase()
		val normalizedArch = when {
			arch.contains("amd64") || arch.contains("x86_64") -> "x86_64"
			arch.contains("arm") -> "arm64"
			else -> arch
		}
		val versionFile = File("currentVersion")
		if (!versionFile.exists()) {
			withContext(Dispatchers.IO) {
				versionFile.createNewFile()
			}
			versionFile.writeText("v0.0.0")
		}
		val currentVersionTag = versionFile.readText()

		 val releases = updaterIO.getReleases()


		if (releases.isEmpty()) {
			return
		}

		val selectedVersion = releases.find { it.platform == os.currentPlatform.name.lowercase() }

		if (currentVersionTag == selectedVersion?.version) {
			state.statusText = "No update found, starting"
			state.subText = ""
			TerminalUtil.info("Using version: $selectedVersion")
			TerminalUtil.info("Current version: $currentVersionTag")
			TerminalUtil.info("No Update available")
			TerminalUtil.info("Launching server")
			withContext(Dispatchers.IO) {
				Thread.sleep(3000)
			}
			launchServer()
			exitProcess(0)
		}

		if (selectedVersion == null) {
			state.hasError = true
			state.errorText = "Could not get selected version"
			return
		}

		state.versionTag = selectedVersion.version
		state.mainProgressIsVisible = true
		state.subProgressIsVisible = true

		when (os.currentPlatform) {
			OperatingSystem.WINDOWS -> {
				val windows = Windows(state, updaterIO)
					windows.updateWindows(
						currentVersionTag,
						selectedVersion.version,
						configDir,
						vrConfig,
						selectedVersion.url,
						selectedVersion.checksum,
						"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"
					)
				}

			OperatingSystem.LINUX -> {
				val linux = Linux(state, updaterIO)
					linux.updateLinux(
						currentVersionTag,
						selectedVersion.version,
						configDir,
						vrConfig,
						selectedVersion.url,
						selectedVersion.checksum,
						"https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"
					)
				}

			OperatingSystem.OSX -> {
				TerminalUtil.error("MacOS currently not supported by the updater")
			}

			OperatingSystem.UNKNOWN -> {
				TerminalUtil.error("Could not determine operating system exiting")
				return
			}
		}

		if (!state.hasError) {
			setUpdateDone()
			saveCurrentVersionTag()
			withContext(Dispatchers.IO) {
				Thread.sleep(1000)
			}
			launchServer()
			exitProcess(0)
		}

	}


	fun resolveConfigDirectory(identifier: String): Path? = when (currentPlatform) {
		OperatingSystem.LINUX -> System.getenv("XDG_CONFIG_HOME")?.let { Path(it, identifier) }
			?: System.getenv("HOME")?.let { Path(it, ".config", identifier) }

		OperatingSystem.WINDOWS -> System.getenv("AppData")?.let { Path(it, identifier) }

		OperatingSystem.OSX -> System.getenv("HOME")?.let { Path(it, "Library", "Application Support", identifier) }

		OperatingSystem.UNKNOWN -> null
	}

	fun resolveConfig(configFilename: String): String {
		if (Path("config/").exists()) {
			return configFilename
		}

		val configFile = resolveConfigDirectory(SLIMEVR_IDENTIFIER)?.resolve(configFilename) ?: return configFilename
		if (!configFile.exists() && Path(configFilename).exists()) {
			//LogManager.info("Moved local config file to appdata folder")
			Files.move(Path(configFilename), configFile)
		}
		return configFile.pathString
	}

	fun setUpdateDone() {
		state.subText = ""
		state.statusText = "Update Done"
		state.mainProgress = 0.0f
		state.subProgress = 0.0f
		state.mainProgressIsVisible = false
		state.subProgressIsVisible = false
	}

	fun saveCurrentVersionTag() {
		val configDir = resolveConfigDirectory(SLIMEVR_IDENTIFIER)
		File("currentVersion").writeText(state.versionTag)
	}
}
