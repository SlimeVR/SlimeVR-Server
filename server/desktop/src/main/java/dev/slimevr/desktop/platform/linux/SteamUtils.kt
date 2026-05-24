package dev.slimevr.desktop.platform.linux

import io.eiren.util.logging.LogManager
import java.io.BufferedReader
import java.io.FileReader
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

class SteamUtils {
	companion object {
		val STEAM_PATH = System.getenv("HOME")?.let { home ->
			Path(home, ".steam", "root").takeIf { it.exists() }
				?: Path(home, ".steam", "debian-installation").takeIf { it.exists() }
				?: Path(home, ".var", "app", "com.valvesoftware.Steam", "data", "Steam").takeIf { it.exists() }
		}
		val VDF_KEY_VALUES_PATTERN = Regex(""""(\w+)"[ \t]*(?:"(.+)")?""")

		fun findAppLibraryLocation(appId: Int): Path? {
			if (STEAM_PATH == null) return null

			try {
				BufferedReader(FileReader(STEAM_PATH.resolve("config/libraryfolders.vdf").toFile())).use { reader ->
					var depth = 0
					var currentLibraryPath: Path? = null

					while (reader.ready()) {
						val line = reader.readLine().trim()
						if (line == "{") {
							depth += 1
							continue
						}
						if (line == "}") {
							depth -= 1
							continue
						}

						VDF_KEY_VALUES_PATTERN.matchEntire(line)?.let {
							val key = it.groupValues[1]
							val value = it.groupValues[2]

							when (depth) {
								0 -> {
									require(key == "libraryfolders") { "root key must be libraryfolders" }
								}

								1 -> {
									// start of a library node
								}

								2 -> {
									// in a library node
									if (key == "path") {
										currentLibraryPath = Path(value)
									} else if (key == "apps") {
										// start of apps node
										require(currentLibraryPath != null) { "path must come before apps in node" }
									}
								}

								3 -> {
									// in apps node
									val curAppId = try {
										key.toInt()
									} catch (_: NumberFormatException) {
										null
									}
									if (curAppId != null && curAppId == appId) {
										check(currentLibraryPath != null) { "path must come before apps in node" }
										return currentLibraryPath
									}
								}
							}
						}
					}
					LogManager.warning("[RegEdit] Couldn't find library folder for appid $appId")
				}
			} catch (e: Exception) {
				LogManager.severe("[RegEdit] Error parsing Steam libraryfolders", e)
			}
			return null
		}
	}
}
