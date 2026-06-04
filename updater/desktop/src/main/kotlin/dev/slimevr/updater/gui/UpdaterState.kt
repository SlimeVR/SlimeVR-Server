package dev.slimevr.updater.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UpdaterState {
	var mainProgress by mutableStateOf(0f)
	var mainProgressIsVisible by mutableStateOf(false)
	var subProgress by mutableStateOf(0f)
	var subProgressIsVisible by mutableStateOf(false)

	var hasError by mutableStateOf(false)

	var statusText by mutableStateOf("")
	var subText by mutableStateOf<String?>(null)
	var errorText by mutableStateOf("")

	var versionTag by mutableStateOf("")
	var serverUrl by mutableStateOf("")
	var openVRDriverUrl by mutableStateOf("")
}
