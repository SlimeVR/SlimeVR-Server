package dev.slimevr.updater

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UpdaterState {
	var mainProgress by mutableStateOf(0f)
	var subProgress by mutableStateOf(0f)

	var statusText by mutableStateOf("Installing SlimeVR")
	var errorMessage by mutableStateOf<String?>(null)

	var version by mutableStateOf("")
}
