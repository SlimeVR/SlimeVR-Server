package dev.slimevr.firmware

object FirmwareManagerBaseBehaviour : FirmwareManagerBehaviour {
	override fun reduce(state: FirmwareManagerState, action: FirmwareManagerActions) = when (action) {
		is FirmwareManagerActions.UpdateJob -> state.copy(
			jobs = state.jobs + (
				action.portLocation to FirmwareJobStatus(
					portLocation = action.portLocation,
					firmwareDeviceId = action.firmwareDeviceId,
					status = action.status,
					progress = action.progress,
				)
			),
		)

		is FirmwareManagerActions.RemoveJob -> state.copy(jobs = state.jobs - action.portLocation)
	}
}