package dev.slimevr.config

import java.nio.file.Path

const val SETTINGS_PROFILE_DIR = "settings"
const val USER_PROFILE_DIR = "user"

private const val PROFILE_FILE_EXTENSION = ".json"
private val PROFILE_NAME = Regex("[A-Za-z0-9 _-]{1,64}")

enum class ProfileKind { SETTINGS, USER }

enum class ProfileStatus { OK, INVALID_NAME, ALREADY_EXISTS, NOT_FOUND, IS_ACTIVE, FAILED }

data class ProfilesState(
	val settings: List<String> = emptyList(),
	val user: List<String> = emptyList(),
)

fun isValidProfileName(name: String): Boolean = PROFILE_NAME.matches(name) && name == name.trim()

/** For names read from disk, which can't be assumed to come from [isValidProfileName]. */
fun isSafeProfileName(name: String): Boolean {
	if (name.isEmpty() || name == "." || name == "..") return false
	// Path parsing follows the platform's separators and rejects characters it can't store, like NUL or COM
	val path = runCatching { Path.of(name) }.getOrNull() ?: return false
	return path.root == null && path.nameCount == 1 && path.fileName.toString() == name
}

fun profileFileName(name: String): String = "$name$PROFILE_FILE_EXTENSION"

/** Profile names found in a storage directory, without backups and temp files. */
fun profileNamesIn(fileNames: List<String>): List<String> = fileNames
	.filter { it.endsWith(PROFILE_FILE_EXTENSION) }
	.map { it.removeSuffix(PROFILE_FILE_EXTENSION) }

/** Names compare case-insensitively since some filesystems do. */
fun hasProfileNamed(names: List<String>, name: String): Boolean = names.any { it.equals(name, ignoreCase = true) }
