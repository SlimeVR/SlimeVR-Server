package dev.slimevr.updater

import java.util.Locale

enum class OperatingSystem(
	val descriptor: String,
	private val aliases: Array<String>,
) {
	LINUX("linux", arrayOf("linux", "unix")),
	WINDOWS("windows", arrayOf("win")),
	OSX("osx", arrayOf("mac")),
	UNKNOWN("unknown", arrayOf()),
	;

	companion object {
		val currentPlatform: OperatingSystem by lazy {
			val osName = System.getProperty("os.name").lowercase(Locale.getDefault())
			OperatingSystem.entries.find { os ->
				os.aliases.any { alias ->
					osName.contains(
						alias,
					)
				}
			}?.let {
				return@lazy it
			}
			UNKNOWN
		}
	}
}
