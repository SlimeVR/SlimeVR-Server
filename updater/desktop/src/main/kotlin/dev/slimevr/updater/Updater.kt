package dev.slimevr.updater

import dev.slimevr.updater.ManifestUtils.Companion.getCurrentVersionTag
import dev.slimevr.updater.ManifestUtils.Companion.getRelease
import dev.slimevr.updater.OperatingSystem.Companion.currentPlatform
import dev.slimevr.updater.util.TerminalUtil
import io.eiren.util.logging.LogManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.system.exitProcess

const val SLIMEVR_IDENTIFIER = "dev.slimevr.SlimeVR"

class Updater(
	val state: UpdaterState,
	val updaterIO: UpdaterIO,
) {
	suspend fun runUpdater() {
		val vrConfig = resolveConfig("vrconfig.yml")
		val configDir = resolveConfigDirectory(SLIMEVR_IDENTIFIER)?.toAbsolutePath().toString()
		LogManager.info("Using config dir: $configDir")
		val manifest = Manifest().getManifest()
		val os = OperatingSystem
		val arch = System.getProperty("os.arch").lowercase()
		val normalizedArch = when {
			arch.contains("amd64") || arch.contains("x86_64") -> "x86_64"
			arch.contains("arm") -> "arm64"
			else -> arch
		}
		val versionFile = File("$configDir/currentVersion")
		if (!versionFile.exists()) {
			withContext(Dispatchers.IO) {
				versionFile.createNewFile()
			}
			versionFile.writeText("v0.0.0")
		}
		val currentVersionTag = versionFile.readText()

		val versionTag: String? = if (featureFlags.version != null) {
			featureFlags.version
		} else {
			getCurrentVersionTag(
				manifest,
				os.currentPlatform.descriptor,
				normalizedArch,
			)
		}

		if (versionTag == null) {
			state.hasError = true
			state.errorText = "Could not get selected version"
			return
		}

		val selectedVersion = getRelease(manifest, "stable", versionTag, os.currentPlatform.descriptor, normalizedArch)
		TerminalUtil.info(selectedVersion.toString())
		if (selectedVersion == null) {
			state.hasError = true
			state.errorText = "Could not get selected version"
			return
		}
		state.versionTag = versionTag

		when (os.currentPlatform) {
			OperatingSystem.WINDOWS -> {
				val windows = Windows(state, updaterIO)
				windows.updateWindows(selectedVersion.url)
			}

			OperatingSystem.LINUX -> {
				val linux = Linux(state, updaterIO)
				linux.updateLinux(currentVersionTag, versionTag, configDir, vrConfig, selectedVersion.url, "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip")
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
			Thread.sleep(500)
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
			LogManager.info("Moved local config file to appdata folder")
			Files.move(Path(configFilename), configFile)
		}
		return configFile.pathString
	}

	fun setUpdateDone() {
		state.subText = ""
		state.statusText = "Update Done"
		state.mainProgress = 0.0f
		state.subProgress = 0.0f
		state.mainProgressisVisible = false
		state.subProgressisVisible = false
	}

	fun saveCurrentVersionTag() {
		val configDir = resolveConfigDirectory(SLIMEVR_IDENTIFIER)
		File("$configDir/currentVersion").writeText(state.versionTag)
	}

	companion object {
		val CDN = "http://127.0.0.1:8080"
	}
}
