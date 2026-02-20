@file:JvmName("Main")

package dev.slimevr.updater

import java.awt.Color
import kotlin.text.ifEmpty

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"
val updaterGui = UpdaterGui()

suspend fun main(args: Array<String>) {
	label.setBounds(0, 0, 250, 50)

	mainProgressBar.value = 0
	mainProgressBar.isStringPainted = true
	mainProgressPanel.add(mainProgressBar)
	mainProgressPanel.setBounds(0, 50, 250, 50)

	subProgressBar.value = 0
	subProgressBar.isStringPainted = true
	subProgressPanel.add(subProgressBar)
	subProgressBar.isVisible = false
	subProgressPanel.setBounds(0, 100, 250, 50)

	frame.background = Color(187, 138, 229)
	frame.setLocationRelativeTo(null)
	frame.isUndecorated = true
	frame.add(label)
	frame.add(mainProgressPanel)
	frame.add(subProgressPanel)
	frame.setSize(250, 300)
	frame.layout = null

	val updater = Updater()
	updater.runUpdater()

	mainProgressBar.value = 100
	mainProgressBar.string = "Done updating"
	subProgressBar.isVisible = false

	if (isUpdateSuccessFull) {
		frame.dispose()
	} else {
		label.text = "The update was not successful!"
		subProgressBar.string = ""
	}
}
