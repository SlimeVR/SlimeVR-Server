package dev.slimevr.desktop.updater

import dev.slimevr.desktop.GIT_CLEAN
import dev.slimevr.desktop.GIT_COMMIT_HASH
import dev.slimevr.desktop.GIT_VERSION_TAG
import kotlin.text.ifEmpty

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"

suspend fun main(args: Array<String>) {

	println("Running updater")
	val updater = Updater()
	updater.runUpdater()
}
