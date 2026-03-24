package dev.slimevr

import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists

const val SLIMEVR_IDENTIFIER = "dev.slimevr.SlimeVR"


enum class Platform {
	LINUX, WINDOWS, OSX, UNKNOWN
}

val CURRENT_PLATFORM: Platform = detectPlatform()

private fun detectPlatform(): Platform {
	val os = System.getProperty("os.name").lowercase(Locale.getDefault())
	if (os.contains("win")) return Platform.WINDOWS
	if (os.contains("mac") || os.contains("darwin")) return Platform.OSX
	if (os.contains("linux") || os.contains("unix")) return Platform.LINUX
	return Platform.UNKNOWN
}

fun getJavaExecutable(forceConsole: Boolean): String {
	val bin = System.getProperty("java.home") + File.separator + "bin" + File.separator

	if (CURRENT_PLATFORM == Platform.WINDOWS && !forceConsole) {
		val javaw = bin + "javaw.exe"
		if (File(javaw).isFile) return javaw
	}

	if (CURRENT_PLATFORM == Platform.WINDOWS) return bin + "java.exe"
	return bin + "java"
}

fun getSocketDirectory(): String {
	val envDir = System.getenv("SLIMEVR_SOCKET_DIR")
	if (envDir != null) return envDir

	if (CURRENT_PLATFORM == Platform.LINUX) {
		val xdg = System.getenv("XDG_RUNTIME_DIR")
		if (xdg != null) return xdg
	}

	return System.getProperty("java.io.tmpdir")
}

fun resolveConfigDirectory(): Path? {
	if (Path("config/").exists()) { // this is only for dev
		return Path("config/")
	}

	val home = System.getenv("HOME")

	return when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> {
			val appData = System.getenv("AppData")
			if (appData != null) Path(appData, SLIMEVR_IDENTIFIER) else null
		}
		Platform.LINUX -> {
			val xdg = System.getenv("XDG_CONFIG_HOME")
			if (xdg != null) Path(xdg, SLIMEVR_IDENTIFIER)
			else if (home != null) Path(home, ".config", SLIMEVR_IDENTIFIER)
			else null
		}
		Platform.OSX -> {
			if (home != null) Path(home, "Library", "Application Support", SLIMEVR_IDENTIFIER) else null
		}
		else -> null
	}
}

fun resolveLogDirectory(): Path? {
	val home = System.getenv("HOME")
	val appData = System.getenv("AppData")

	return when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> if (appData != null) Path(appData, SLIMEVR_IDENTIFIER, "logs") else null
		Platform.OSX -> if (home != null) Path(home, "Library", "Logs", SLIMEVR_IDENTIFIER) else null
		Platform.LINUX -> {
			val xdg = System.getenv("XDG_DATA_HOME")
			if (xdg != null) Path(xdg, SLIMEVR_IDENTIFIER, "logs")
			else if (home != null) Path(home, ".local", "share", SLIMEVR_IDENTIFIER, "logs")
			else null
		}
		else -> null
	}
}
