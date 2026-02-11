@file:JvmName("Main")

package dev.slimevr.updater

import dev.slimevr.updater.GIT_CLEAN
import dev.slimevr.updater.GIT_COMMIT_HASH
import dev.slimevr.updater.GIT_VERSION_TAG
import kotlin.text.ifEmpty

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"

suspend fun main(args: Array<String>) {
	println("Running updater")
	val updater = Updater()
	updater.runUpdater()
}
