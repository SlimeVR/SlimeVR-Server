package dev.slimevr.solarxr

import dev.slimevr.heightcalibration.HeightCalibrationManager
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.CancelUserHeightCalibration
import solarxr_protocol.rpc.StartUserHeightCalibration
import solarxr_protocol.rpc.UserHeightRecordingStatusResponse

class HeightCalibrationBehaviour(
	private val heightCalibrationManager: HeightCalibrationManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<StartUserHeightCalibration> {
			heightCalibrationManager.start()
		}

		receiver.rpcDispatcher.on<CancelUserHeightCalibration> {
			heightCalibrationManager.cancel()
		}

		heightCalibrationManager.context.state.drop(1).onEach { state ->
			receiver.sendRpc(
				UserHeightRecordingStatusResponse(
					status = state.status,
					hmdheight = state.currentHeight,
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
