package dev.slimevr.provisioning

import dev.slimevr.provisioning.ProvisioningManager.Companion.INITIAL_STATE

object ProvisioningManagerBaseBehaviour : ProvisioningManagerBehaviour {
	override fun reduce(state: ProvisioningManagerState, action: ProvisioningActions) = when (action) {
		is ProvisioningActions.PortSelected -> state.copy(
			portLocation = action.portLocation,
			macAddress = null,
		)

		is ProvisioningActions.StatusChanged -> state.copy(
			status = action.status,
		)

		is ProvisioningActions.MacAddressObtained -> state.copy(
			macAddress = action.mac,
		)

		is ProvisioningActions.Clear -> INITIAL_STATE
	}
}
