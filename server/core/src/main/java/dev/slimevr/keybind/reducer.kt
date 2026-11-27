package dev.slimevr.keybind

fun reduce(state: KeybindState, action: KeybindActions): KeybindState = when (action) {
	is KeybindActions.SetRecording -> state.copy(recording = action.recording)
}
