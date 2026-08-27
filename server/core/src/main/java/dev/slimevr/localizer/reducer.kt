package dev.slimevr.localizer

fun reduce(state: LocalizerState, action: LocalizerActions): LocalizerState = when (action) {
    is LocalizerActions.Reset -> state // TODO

    is LocalizerActions.SetHeadPosition -> state.copy(headPosition = action.position)
}
