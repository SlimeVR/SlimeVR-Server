package dev.slimevr.updater

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UpdaterState {
	var mainProgress by mutableStateOf(0f)
	var mainProgressisVisible by mutableStateOf(true)
	var subProgress by mutableStateOf(0f)
	var subProgressisVisible by mutableStateOf(true)

	var hasError by mutableStateOf(false)

	var statusText by mutableStateOf("Installing SlimeVR")
	var subText by mutableStateOf<String?>(null)
	var errorText by mutableStateOf("")

	var versionTag by mutableStateOf("")
	var serverUrl by mutableStateOf("")
	var openVRDriverUrl by mutableStateOf("")
}
