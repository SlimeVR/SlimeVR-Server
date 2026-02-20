@file:JvmName("Main")

package dev.slimevr.updater

import java.awt.Color
import kotlin.text.ifEmpty

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"
val updaterGui = UpdaterGui()

suspend fun main(args: Array<String>) {

	val updater = Updater()
	updater.runUpdater()
}
