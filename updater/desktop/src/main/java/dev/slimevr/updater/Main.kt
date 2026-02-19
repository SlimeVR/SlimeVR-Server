@file:JvmName("Main")

package dev.slimevr.updater

import java.awt.Label
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.text.ifEmpty

val frame = JFrame("SlimeVR Updater")
val label = Label("Running Updater")
val mainProgressPanel = JPanel()
val mainProgressBar = JProgressBar()
val subProgressPanel = JPanel()
val subProgressBar = JProgressBar()
val isUpdateSuccessFull = true

val VERSION =
	(GIT_VERSION_TAG.ifEmpty { GIT_COMMIT_HASH }) +
		if (GIT_CLEAN) "" else "-dirty"

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
	}
	else {
		label.text = "The update was not successfull!"
		subProgressBar.string = ""
	}
}
