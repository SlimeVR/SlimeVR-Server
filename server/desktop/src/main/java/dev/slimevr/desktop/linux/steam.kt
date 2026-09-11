package dev.slimevr.desktop.linux

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

internal val STEAM_PATH = System.getenv("HOME")?.let { home ->
	listOf(
		Path(home, ".steam", "root"),
		Path(home, ".steam", "debian-installation"),
		Path(home, ".var", "app", "com.valvesoftware.Steam", "data", "Steam"),
	).firstOrNull { it.exists() }
}
internal val VDF_KEY_VALUES_PATTERN = Regex(""""(\w+)"[ \t]*(?:"(.+)")?""")

suspend fun findAppLibraryLocation(appId: Int): Path = withContext(Dispatchers.IO) {
	if (STEAM_PATH == null) throw FileNotFoundException("Unable to find Steam")
	val libraryFoldersPath = STEAM_PATH.resolve("config/libraryfolders.vdf")
	if (!libraryFoldersPath.exists()) throw FileNotFoundException("Steam libraryfolders.vdf does not exist")

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
							return@withContext currentLibraryPath
						}
					}
				}
			}
		}
		throw RuntimeException("Couldn't find library folder for appid $appId")
	}
}
