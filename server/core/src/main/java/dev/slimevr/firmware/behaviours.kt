package dev.slimevr.firmware

object FirmwareManagerBaseBehaviour : FirmwareManagerBehaviour {
	override fun reduce(state: FirmwareManagerState, action: FirmwareManagerActions) = when (action) {
		is FirmwareManagerActions.UpdateJob -> state.copy(
			jobs = state.jobs + (action.jobStatus.portLocation to action.jobStatus),
		)
		is FirmwareManagerActions.RemoveJob -> state.copy(jobs = state.jobs - action.portLocation)
		is FirmwareManagerActions.ClearJobs -> state.copy(jobs = mapOf())
	}
}
