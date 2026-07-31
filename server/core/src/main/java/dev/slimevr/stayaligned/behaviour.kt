package dev.slimevr.stayaligned

class StayAlignedBasicBehaviour : StayAlignedBehaviour {
    override fun reduce(state: StayAlignedState, action: StayAlignedActions) = when (action) {
        is StayAlignedActions.SetHideYawCorrection -> state.copy(hideYawCorrection = action.hideYawCorrection)
    }
}
